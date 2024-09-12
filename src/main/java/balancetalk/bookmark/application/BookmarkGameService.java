package balancetalk.bookmark.application;

import static balancetalk.bookmark.domain.BookmarkType.*;
import static balancetalk.global.notification.domain.NotificationMessage.GAME_BOOKMARK;
import static balancetalk.global.notification.domain.NotificationMessage.GAME_BOOKMARK_100;
import static balancetalk.global.notification.domain.NotificationMessage.GAME_BOOKMARK_1000;
import static balancetalk.global.notification.domain.NotificationStandard.FIRST_STANDARD_OF_NOTIFICATION;
import static balancetalk.global.notification.domain.NotificationStandard.FOURTH_STANDARD_OF_NOTIFICATION;
import static balancetalk.global.notification.domain.NotificationStandard.SECOND_STANDARD_OF_NOTIFICATION;
import static balancetalk.global.notification.domain.NotificationStandard.THIRD_STANDARD_OF_NOTIFICATION;
import static balancetalk.global.notification.domain.NotificationTitleCategory.WRITTEN_GAME;

import balancetalk.bookmark.domain.Bookmark;
import balancetalk.bookmark.domain.BookmarkGenerator;
import balancetalk.bookmark.domain.BookmarkRepository;
import balancetalk.game.domain.Game;
import balancetalk.game.domain.GameReader;
import balancetalk.game.domain.GameSet;
import balancetalk.game.domain.repository.GameSetRepository;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.global.notification.application.NotificationService;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkGameService {

    private final GameReader gameReader;
    private final BookmarkRepository bookmarkRepository;
    private final BookmarkGenerator bookmarkGenerator;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    public void createBookmark(final Long gameId, final ApiMember apiMember) {

        Game game = gameReader.readById(gameId);
        Member member = apiMember.toMember(memberRepository);

        GameSet gameSet = game.getGameSet(gameId);

        if (member.isMyGameSet(gameSet)) {
            throw new BalanceTalkException(ErrorCode.CANNOT_BOOKMARK_MY_RESOURCE);
        }

        if (member.hasBookmarked(gameId, GAME)) {
            throw new BalanceTalkException(ErrorCode.ALREADY_BOOKMARKED);
        }

        member.getBookmarkOf(gameId, GAME)
                .ifPresentOrElse(Bookmark::activate,
                        () -> bookmarkRepository.save(bookmarkGenerator.generate(gameId, GAME, member)));
        game.increaseBookmarks();
    }

    public void deleteBookmark(final Long gameId, final ApiMember apiMember) {
        Game game = gameReader.readById(gameId);
        Member member = apiMember.toMember(memberRepository);

        Bookmark bookmark = member.getBookmarkOf(gameId, GAME)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_BOOKMARK));

        if (!bookmark.isActive()) {
            throw new BalanceTalkException(ErrorCode.ALREADY_DELETED_BOOKMARK);
        }
        bookmark.deactivate();
        game.decreaseBookmarks();
        sendBookmarkGameNotification(game);
    }

    private void sendBookmarkGameNotification(Game game) {
        Member member = null; // TODO: GameSet으로 변경됨에 따라 수정 필요
        long bookmarkedCount = game.getBookmarks();
        String bookmarkCountKey = "BOOKMARK_" + bookmarkedCount;
        Map<String, Boolean> notificationHistory = game.getNotificationHistory();
        String category = WRITTEN_GAME.getCategory();

        boolean isMilestoneBookmarked = (bookmarkedCount == FIRST_STANDARD_OF_NOTIFICATION.getCount() ||
                bookmarkedCount == SECOND_STANDARD_OF_NOTIFICATION.getCount() ||
                bookmarkedCount == THIRD_STANDARD_OF_NOTIFICATION.getCount() ||
                (bookmarkedCount > THIRD_STANDARD_OF_NOTIFICATION.getCount() &&
                        bookmarkedCount % THIRD_STANDARD_OF_NOTIFICATION.getCount() == 0) ||
                (bookmarkedCount > FOURTH_STANDARD_OF_NOTIFICATION.getCount() &&
                        bookmarkedCount % FOURTH_STANDARD_OF_NOTIFICATION.getCount() == 0));

        // 북마크 개수가 10, 50, 100*n개, 1000*n개 일 때 알림
        if (isMilestoneBookmarked && !notificationHistory.getOrDefault(bookmarkCountKey, false)) {
            notificationService.sendGameNotification(member, game, category, GAME_BOOKMARK.format(bookmarkedCount));
            // 북마크 개수가 100개일 때 배찌 획득 알림
            if (bookmarkedCount == THIRD_STANDARD_OF_NOTIFICATION.getCount()) {
                notificationService.sendGameNotification(member, game, category, GAME_BOOKMARK_100.getMessage());
            }
            // 북마크 개수가 1000개일 때 배찌 획득 알림
            else if (bookmarkedCount == FOURTH_STANDARD_OF_NOTIFICATION.getCount()) {
                notificationService.sendGameNotification(member, game, category, GAME_BOOKMARK_1000.getMessage());
            }
            notificationHistory.put(bookmarkCountKey, true);
            game.setNotificationHistory(notificationHistory);
        }
    }
}
