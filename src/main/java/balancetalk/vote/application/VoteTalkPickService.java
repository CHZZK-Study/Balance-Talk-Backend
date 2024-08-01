package balancetalk.vote.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.member.dto.GuestOrApiMember;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.TalkPickReader;
import balancetalk.vote.domain.Vote;
import balancetalk.vote.domain.VoteRepository;
import balancetalk.vote.dto.VoteTalkPickDto.VoteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteTalkPickService {

    private final TalkPickReader talkPickReader;
    private final VoteRepository voteRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createVote(long talkPickId, VoteRequest request, GuestOrApiMember guestOrApiMember) {
        TalkPick talkPick = talkPickReader.readById(talkPickId);

        if (guestOrApiMember.isGuest()) {
            voteRepository.save(request.toEntity(null, talkPick));
            return;
        }

        Member member = guestOrApiMember.toMember(memberRepository);
        if (member.hasVotedTalkPick(talkPick)) {
            throw new BalanceTalkException(ErrorCode.ALREADY_VOTE);
        }

        voteRepository.save(request.toEntity(member, talkPick));
    }

    @Transactional
    public void updateVote(long talkPickId, VoteRequest request, ApiMember apiMember) {
        TalkPick talkPick = talkPickReader.readById(talkPickId);
        Member member = apiMember.toMember(memberRepository);

        Optional<Vote> vote = member.getVoteOnTalkPick(talkPick);
        if (vote.isEmpty()) {
            throw new BalanceTalkException(ErrorCode.NOT_FOUND_VOTE);
        }

        vote.get().updateVoteOption(request.getVoteOption());
    }

    @Transactional
    public void deleteVote(long talkPickId, ApiMember apiMember) {
        TalkPick talkPick = talkPickReader.readById(talkPickId);
        Member member = apiMember.toMember(memberRepository);

        Optional<Vote> vote = member.getVoteOnTalkPick(talkPick);
        if (vote.isEmpty()) {
            throw new BalanceTalkException(ErrorCode.NOT_FOUND_VOTE);
        }

        voteRepository.delete(vote.get());
    }
}
