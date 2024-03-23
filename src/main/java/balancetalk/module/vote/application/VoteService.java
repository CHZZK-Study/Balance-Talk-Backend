package balancetalk.module.vote.application;

import static balancetalk.global.exception.ErrorCode.*;
import static balancetalk.global.utils.SecurityUtils.*;

import balancetalk.global.exception.BalanceTalkException;
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

    public Vote createVote(Long postId, VoteRequest voteRequest, String token) {
        Post post = getPost(postId);
        if (post.hasDeadlineExpired()) {
            throw new BalanceTalkException(EXPIRED_POST_DEADLINE);
        }

        BalanceOption balanceOption = getBalanceOption(voteRequest);
        if (post.notContainsBalanceOption(balanceOption)) {
            throw new BalanceTalkException(MISMATCHED_BALANCE_OPTION);
        }

        if (token == null) {
            return voteForGuest(voteRequest, balanceOption);
        }
        return voteForMember(voteRequest, post, balanceOption);
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_POST));
    }

    private BalanceOption getBalanceOption(VoteRequest voteRequest) {
        return balanceOptionRepository.findById(voteRequest.getSelectedOptionId())
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_BALANCE_OPTION));
    }

    private Vote voteForMember(VoteRequest voteRequest, Post post, BalanceOption balanceOption) {
        Member member = getCurrentMember(memberRepository);

        if (member.hasVoted(post)) {
            throw new BalanceTalkException(ALREADY_VOTE);
        }

        return voteRepository.save(voteRequest.toEntityWithMember(balanceOption, member));
    }

    private Vote voteForGuest(VoteRequest voteRequest, BalanceOption balanceOption) {
        return voteRepository.save(voteRequest.toEntity(balanceOption));
    }

    @Transactional(readOnly = true)
    public List<VotingStatusResponse> votingStatus(Long postId) {
        Post post = getPost(postId);

        List<BalanceOption> options = post.getOptions();
        List<VotingStatusResponse> responses = new ArrayList<>();

        for (BalanceOption option : options) {
            VotingStatusResponse votingStatusResponse = VotingStatusResponse.builder()
                    .optionTitle(option.getTitle())
                    .voteCount(option.voteCount())
                    .build();
            responses.add(votingStatusResponse);
        }

        return responses;
    }

    public Vote updateVote(Long postId, VoteRequest voteRequest) {
        Post post = getPost(postId);
        if (post.isCasual()) {
            throw new BalanceTalkException(UNMODIFIABLE_VOTE);
        }
        BalanceOption newSelectedOption = getBalanceOption(voteRequest);
        Member member = getCurrentMember(memberRepository);
        Vote participatedVote = getParticipatedVote(post, member);

        return participatedVote.changeBalanceOption(newSelectedOption);
    }

    private Vote getParticipatedVote(Post post, Member member) {
        return member.getVotes().stream()
                .filter(vote -> vote.getBalanceOption().getPost().equals(post))
                .findFirst()
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_VOTE));
    }
}
