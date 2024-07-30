package balancetalk.bookmark.application;

import balancetalk.bookmark.domain.Bookmark;
import balancetalk.bookmark.domain.BookmarkGenerator;
import balancetalk.bookmark.domain.BookmarkRepository;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.talkpick.domain.TalkPickValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static balancetalk.bookmark.domain.BookmarkType.TALK_PICK;
import static balancetalk.global.exception.ErrorCode.ALREADY_BOOKMARK;
import static balancetalk.global.exception.ErrorCode.CANNOT_BOOKMARK_MY_RESOURCE;

@Service
@RequiredArgsConstructor
public class BookmarkTalkPickService {

    private final MemberRepository memberRepository;
    private final TalkPickValidator talkPickValidator;
    private final BookmarkGenerator bookmarkGenerator;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public void createBookmark(final long talkPickId, final ApiMember apiMember) {
        talkPickValidator.validateExistence(talkPickId);

        Member member = apiMember.toMember(memberRepository);
        if (member.hasBookmarked(talkPickId, TALK_PICK)) {
            throw new BalanceTalkException(ALREADY_BOOKMARK);
        }
        if (member.isMyTalkPick(talkPickId)) {
            throw new BalanceTalkException(CANNOT_BOOKMARK_MY_RESOURCE);
        }

        Bookmark bookmark = bookmarkGenerator.generate(talkPickId, TALK_PICK, member);
        bookmarkRepository.save(bookmark);
    }
}
