package balancetalk.module.vote.application;

import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.post.domain.BalanceOption;
import balancetalk.module.post.domain.BalanceOptionRepository;
import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostRepository;
import balancetalk.module.vote.domain.Vote;
import balancetalk.module.vote.domain.VoteRepository;
import balancetalk.module.vote.dto.VoteRequest;
import balancetalk.module.vote.dto.VotingStatusResponse;
import java.util.ArrayList;
import java.util.List;
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
    private final PostRepository postRepository;

    public Vote createVote(VoteRequest voteRequest) {
        BalanceOption balanceOption = balanceOptionRepository.findById(voteRequest.getSelectedOptionId())
                .orElseThrow(); // TODO 예외 처리
        Member member = memberRepository.findById(voteRequest.getMemberId())
                .orElseThrow(); // TODO 예외 처리
        return voteRepository.save(voteRequest.toEntity(balanceOption, member));
    }

    public List<VotingStatusResponse> readVotingStatus(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow();

        List<BalanceOption> options = post.getOptions();
        List<VotingStatusResponse> responses = new ArrayList<>();

        for (BalanceOption option : options) {
            responses.add(
                    new VotingStatusResponse(option.getTitle(), option.getVotes().size()));
        }

        return responses;
    }

    public Vote updateVote(Long postId, VoteRequest voteRequest) {
        Post post = postRepository.findById(postId)
                .orElseThrow();

        if (post.isCasual()) {
            throw new IllegalArgumentException(); // TODO 예외 처리
        }

        Member member = memberRepository.findById(voteRequest.getMemberId())
                .orElseThrow();
        BalanceOption newSelectedOption = balanceOptionRepository.findById(voteRequest.getSelectedOptionId())
                .orElseThrow();
        Vote findVote = member.getVotes().stream()
                .filter(vote -> vote.getBalanceOption().getPost().equals(post))
                .findFirst()
                .orElseThrow();
        return findVote.changeBalanceOption(newSelectedOption);
    }
}
