package balancetalk.vote.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.GuestOrApiMember;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.TalkPickReader;
import balancetalk.vote.domain.VoteRepository;
import balancetalk.vote.dto.VoteTalkPickDto.VoteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteTalkPickService {

    private final TalkPickReader talkPickReader;
    private final VoteRepository voteRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createVote(long talkPickId, VoteRequest request, GuestOrApiMember guestOrApiMember) {
        TalkPick talkPick = talkPickReader.readTalkPickById(talkPickId);

        if (guestOrApiMember.isGuest()) {
            voteRepository.save(request.toEntity(null, talkPick));
            return;
        }

        Member member = guestOrApiMember.toMember(memberRepository);
        if (member.hasVoted(talkPick)) {
            throw new BalanceTalkException(ErrorCode.ALREADY_VOTE);
        }

        voteRepository.save(request.toEntity(member, talkPick));
    }
}
