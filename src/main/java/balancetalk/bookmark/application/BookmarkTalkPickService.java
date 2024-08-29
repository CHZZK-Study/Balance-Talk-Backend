package balancetalk.bookmark.application;

import balancetalk.bookmark.domain.Bookmark;
import balancetalk.bookmark.domain.BookmarkGenerator;
import balancetalk.bookmark.domain.BookmarkRepository;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.notification.application.NotificationService;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.TalkPickReader;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static balancetalk.bookmark.domain.BookmarkType.TALK_PICK;
import static balancetalk.global.exception.ErrorCode.*;
import static balancetalk.global.notification.domain.NotificationMessage.COMMENT_LIKE;
import static balancetalk.global.notification.domain.NotificationMessage.COMMENT_LIKE_100;
import static balancetalk.global.notification.domain.NotificationMessage.COMMENT_LIKE_1000;
import static balancetalk.global.notification.domain.NotificationMessage.TALK_PICK_BOOKMARK;
import static balancetalk.global.notification.domain.NotificationMessage.TALK_PICK_BOOKMARK_100;
import static balancetalk.global.notification.domain.NotificationMessage.TALK_PICK_BOOKMARK_1000;
import static balancetalk.global.notification.domain.NotificationStandard.FIRST_STANDARD_OF_NOTIFICATION;
import static balancetalk.global.notification.domain.NotificationStandard.FOURTH_STANDARD_OF_NOTIFICATION;
import static balancetalk.global.notification.domain.NotificationStandard.SECOND_STANDARD_OF_NOTIFICATION;
import static balancetalk.global.notification.domain.NotificationStandard.THIRD_STANDARD_OF_NOTIFICATION;
import static balancetalk.global.notification.domain.NotificationTitleCategory.OTHERS_TALK_PICK;
import static balancetalk.global.notification.domain.NotificationTitleCategory.WRITTEN_TALK_PICK;

@Service
@RequiredArgsConstructor
public class BookmarkTalkPickService {

    private final TalkPickReader talkPickReader;
    private final MemberRepository memberRepository;
    private final BookmarkGenerator bookmarkGenerator;
    private final BookmarkRepository bookmarkRepository;
    private final NotificationService notificationService;

    @Transactional
    public void createBookmark(final long talkPickId, final ApiMember apiMember) {
        TalkPick talkPick = talkPickReader.readById(talkPickId);
        Member member = apiMember.toMember(memberRepository);

        if (member.isMyTalkPick(talkPick)) {
            throw new BalanceTalkException(CANNOT_BOOKMARK_MY_RESOURCE);
        }
        if (member.hasBookmarked(talkPickId, TALK_PICK)) {
            throw new BalanceTalkException(ALREADY_BOOKMARKED);
        }

        member.getBookmarkOf(talkPickId, TALK_PICK)
                .ifPresentOrElse(Bookmark::activate,
                        () -> bookmarkRepository.save(bookmarkGenerator.generate(talkPickId, TALK_PICK, member)));
        talkPick.increaseBookmarks();
        sendBookmarkTalkPickNotification(talkPick);
    }

    @Transactional
    public void deleteBookmark(Long talkPickId, ApiMember apiMember) {
        TalkPick talkPick = talkPickReader.readById(talkPickId);
        Member member = apiMember.toMember(memberRepository);

        Bookmark bookmark = member.getBookmarkOf(talkPickId, TALK_PICK)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_BOOKMARK));

        if (isNotActivated(bookmark)) {
            throw new BalanceTalkException(ALREADY_DELETED_BOOKMARK);
        }

        bookmark.deactivate();
        talkPick.decreaseBookmarks();
    }

    private boolean isNotActivated(Bookmark bookmark) {
        return !bookmark.isActive();
    }

    private void sendBookmarkTalkPickNotification(TalkPick talkPick) {
        Member member = talkPick.getMember();
        long bookmarkedCount = talkPick.getBookmarks();
        String bookmarkCountKey = "BOOKMARK_" + bookmarkedCount;
        Map<String, Boolean> notificationHistory = talkPick.getNotificationHistory();
        String category = WRITTEN_TALK_PICK.getCategory();

        boolean isMilestoneBookmarked = (bookmarkedCount == FIRST_STANDARD_OF_NOTIFICATION.getCount() ||
                bookmarkedCount == SECOND_STANDARD_OF_NOTIFICATION.getCount() ||
                bookmarkedCount == THIRD_STANDARD_OF_NOTIFICATION.getCount() ||
                (bookmarkedCount > THIRD_STANDARD_OF_NOTIFICATION.getCount() &&
                        bookmarkedCount % THIRD_STANDARD_OF_NOTIFICATION.getCount() == 0) ||
                (bookmarkedCount > FOURTH_STANDARD_OF_NOTIFICATION.getCount() &&
                        bookmarkedCount % FOURTH_STANDARD_OF_NOTIFICATION.getCount() == 0));

        // 북마크 개수가 10, 50, 100*n개, 1000*n개 일 때 알림
        if (isMilestoneBookmarked && !notificationHistory.getOrDefault(bookmarkCountKey, false)) {
            notificationService.sendTalkPickNotification(member, talkPick, category, TALK_PICK_BOOKMARK.format(bookmarkedCount));
            // 북마크 개수가 100개일 때 배찌 획득 알림
            if (bookmarkedCount == THIRD_STANDARD_OF_NOTIFICATION.getCount()) {
                notificationService.sendTalkPickNotification(member, talkPick, category, TALK_PICK_BOOKMARK_100.getMessage());
            }
            // 북마크 개수가 1000개일 때 배찌 획득 알림
            else if (bookmarkedCount == FOURTH_STANDARD_OF_NOTIFICATION.getCount()) {
                notificationService.sendTalkPickNotification(member, talkPick, category, TALK_PICK_BOOKMARK_1000.getMessage());
            }
            notificationHistory.put(bookmarkCountKey, true);
            talkPick.setNotificationHistory(notificationHistory);
        }
    }
}
