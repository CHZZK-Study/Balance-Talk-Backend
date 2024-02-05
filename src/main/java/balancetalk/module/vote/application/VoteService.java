package balancetalk.module.vote.application;

import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.post.domain.BalanceOption;
import balancetalk.module.post.domain.BalanceOptionRepository;
import balancetalk.module.vote.domain.Vote;
import balancetalk.module.vote.domain.VoteRepository;
import balancetalk.module.vote.dto.VoteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final MemberRepository memberRepository;
    private final BalanceOptionRepository balanceOptionRepository;

    public Vote createVote(VoteRequest voteRequest) {
        BalanceOption balanceOption = balanceOptionRepository.findById(voteRequest.getSelectedOptionId())
                .orElseThrow(); // TODO 예외 처리
        Member member = memberRepository.findById(voteRequest.getMemberId())
                .orElseThrow(); // TODO 예외 처리
        return voteRepository.save(voteRequest.toEntity(balanceOption, member));
    }
}
