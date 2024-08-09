package balancetalk.bookmark.application;

import static balancetalk.bookmark.domain.BookmarkType.*;

import balancetalk.bookmark.domain.Bookmark;
import balancetalk.bookmark.domain.BookmarkGenerator;
import balancetalk.bookmark.domain.BookmarkRepository;
import balancetalk.bookmark.domain.BookmarkType;
import balancetalk.game.domain.Game;
import balancetalk.game.domain.GameReader;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
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

    public void createBookmark(final Long gameId, final ApiMember apiMember) {
        Game game = gameReader.readById(gameId);
        Member member = apiMember.toMember(memberRepository);

        if (member.isMyGame(game)) {
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
    }
}
