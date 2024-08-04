package balancetalk.bookmark.application;

import balancetalk.bookmark.domain.Bookmark;
import balancetalk.bookmark.domain.BookmarkGenerator;
import balancetalk.bookmark.domain.BookmarkRepository;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.repository.TalkPickRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static balancetalk.bookmark.domain.BookmarkType.TALK_PICK;
import static balancetalk.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class BookmarkTalkPickService {

    private final TalkPickRepository talkPickRepository;
    private final MemberRepository memberRepository;
    private final BookmarkGenerator bookmarkGenerator;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public void createBookmark(final long talkPickId, final ApiMember apiMember) {
        TalkPick talkPick = talkPickRepository.findById(talkPickId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_TALK_PICK));

        Member member = apiMember.toMember(memberRepository);
        if (member.isMyTalkPick(talkPick)) {
            throw new BalanceTalkException(CANNOT_BOOKMARK_MY_RESOURCE);
        }

        member.getBookmarkOf(talkPickId, TALK_PICK)
                .ifPresentOrElse(Bookmark::activate,
                        () -> bookmarkRepository.save(bookmarkGenerator.generate(talkPickId, TALK_PICK, member)));

        talkPick.increaseBookmarks();
    }

    @Transactional
    public void deleteBookmark(Long talkPickId, ApiMember apiMember) {
        TalkPick talkPick = talkPickRepository.findById(talkPickId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_TALK_PICK));

        Member member = apiMember.toMember(memberRepository);

        Bookmark bookmark = member.getBookmarkOf(talkPickId, TALK_PICK)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_BOOKMARK));

        bookmark.deactivate();

        talkPick.decreaseBookmarks();
    }
}
