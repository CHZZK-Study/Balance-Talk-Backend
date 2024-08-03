package balancetalk.talkpick.application;

import balancetalk.bookmark.domain.repository.BookmarkRepository;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.GuestOrApiMember;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.TalkPickReader;
import balancetalk.talkpick.dto.TalkPickDto.TalkPickDetailResponse;
import balancetalk.vote.domain.Vote;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static balancetalk.bookmark.domain.BookmarkType.TALK_PICK;

@Service
@RequiredArgsConstructor
public class TalkPickService {

    private final TalkPickReader talkPickReader;
    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public TalkPickDetailResponse findById(Long talkPickId, GuestOrApiMember guestOrApiMember) {
        TalkPick talkPick = talkPickReader.readById(talkPickId);
        talkPick.increaseViews();

        long bookmarksCount = bookmarkRepository.countBookmarksByResourceIdAndType(talkPickId, TALK_PICK);

        if (guestOrApiMember.isGuest()) {
            return TalkPickDetailResponse.from(talkPick, bookmarksCount, false, null);
        }

        Member member = guestOrApiMember.toMember(memberRepository);
        boolean hasBookmarked = member.hasBookmarked(talkPickId, TALK_PICK);
        Optional<Vote> myVote = member.getVoteOnTalkPick(talkPick);

        if (myVote.isEmpty()) {
            return TalkPickDetailResponse.from(talkPick, bookmarksCount, hasBookmarked, null);
        }

        return TalkPickDetailResponse.from(talkPick, bookmarksCount, hasBookmarked, myVote.get().getVoteOption());
    }
}
