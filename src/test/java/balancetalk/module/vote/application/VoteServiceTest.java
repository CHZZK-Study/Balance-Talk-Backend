package balancetalk.module.vote.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @InjectMocks
    VoteService voteService;

    @Mock
    VoteRepository voteRepository;

    @Mock
    BalanceOptionRepository balanceOptionRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    PostRepository postRepository;

    @Test
    @DisplayName("회원과 선택지 정보를 바탕으로 투표를 생성한다.")
    void createVote_Success() {
        // given
        BalanceOption option = createBalanceOption(1L, "A", List.of());
        Member member = createMember(1L);
        Vote vote = Vote.builder()
                .id(1L)
                .member(member)
                .balanceOption(option)
                .build();

        when(balanceOptionRepository.findById(any())).thenReturn(Optional.of(option));
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(voteRepository.save(any())).thenReturn(vote);

        // when
        Vote createdVote = voteService.createVote(new VoteRequest(member.getId(), option.getId()));

        // then
        assertThat(createdVote.getMember()).isEqualTo(member);
        assertThat(createdVote.getBalanceOption()).isEqualTo(option);
    }

    @Test
    void readVotingStatus_Success() {
        // given
        List<Vote> votesForA = new ArrayList<>();
        for (long i = 0; i < 5; i++) {
            votesForA.add(Vote.builder().id(i).build());
        }
        List<Vote> votesForB = new ArrayList<>();
        for (long i = 0; i < 3; i++) {
            votesForB.add(Vote.builder().id(i).build());
        }
        List<BalanceOption> options =
                List.of(createBalanceOption(1L, "A", votesForA), createBalanceOption(2L, "B", votesForB));

        Post post = Post.builder()
                .id(1L)
                .options(options)
                .build();

        when(postRepository.findById(any())).thenReturn(Optional.of(post));

        // when
        List<VotingStatusResponse> votingStatusResponses = voteService.readVotingStatus(1L);

        // then
        assertThat(votingStatusResponses.get(0).getOptionTitle()).isEqualTo("A");
        assertThat(votingStatusResponses.get(0).getVoteCount()).isEqualTo(5);
        assertThat(votingStatusResponses.get(1).getOptionTitle()).isEqualTo("B");
        assertThat(votingStatusResponses.get(1).getVoteCount()).isEqualTo(3);
    }

    private BalanceOption createBalanceOption(Long id, String title, List<Vote> votes) {
        return BalanceOption.builder()
                .id(id)
                .title(title)
                .votes(votes)
                .build();
    }

    private Member createMember(Long id) {
        return Member.builder()
                .id(id)
                .build();
    }
}