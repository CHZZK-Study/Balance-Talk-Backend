package balancetalk.talkpick.application;

import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.GuestOrApiMember;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.TalkPickReader;
import balancetalk.talkpick.domain.repository.TalkPickRepository;
import balancetalk.talkpick.dto.TalkPickDto.TalkPickDetailResponse;
import balancetalk.vote.domain.Vote;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static balancetalk.bookmark.domain.BookmarkType.TALK_PICK;
import static balancetalk.talkpick.dto.TalkPickDto.TalkPickResponse;

@Service
@RequiredArgsConstructor
public class TalkPickService {

    private final TalkPickReader talkPickReader;
    private final MemberRepository memberRepository;
    private final TalkPickRepository talkPickRepository;

    @Transactional
    public TalkPickDetailResponse findById(Long talkPickId, GuestOrApiMember guestOrApiMember) {
        TalkPick talkPick = talkPickReader.readById(talkPickId);
        talkPick.increaseViews();

        if (guestOrApiMember.isGuest()) {
            return TalkPickDetailResponse.from(talkPick, false, null);
        }

        Member member = guestOrApiMember.toMember(memberRepository);
        boolean hasBookmarked = member.hasBookmarked(talkPickId, TALK_PICK);
        Optional<Vote> myVote = member.getVoteOnTalkPick(talkPick);

        if (myVote.isEmpty()) {
            return TalkPickDetailResponse.from(talkPick, hasBookmarked, null);
        }

        return TalkPickDetailResponse.from(talkPick, hasBookmarked, myVote.get().getVoteOption());
    }

    public Page<TalkPickResponse> findPaged(Pageable pageable) {
        return talkPickRepository.findPagedTalkPicks(pageable);
    }

    public List<TalkPickResponse> findBestTalkPicks() {
        return talkPickRepository.findBestTalkPicks();
    }
}
