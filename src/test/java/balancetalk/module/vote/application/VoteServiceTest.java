package balancetalk.module.vote.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.post.domain.*;
import balancetalk.module.vote.domain.Vote;
import balancetalk.module.vote.domain.VoteRepository;
import balancetalk.module.vote.dto.VoteRequest;
import balancetalk.module.vote.dto.VotingStatusResponse;
import java.time.LocalDateTime;
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
        Post post = Post.builder()
                .id(1L)
                .deadline(LocalDateTime.now().plusDays(1))
                .options(List.of(option))
                .build();
        Member member = createMember(1L);
        Vote vote = Vote.builder()
                .id(1L)
                .member(member)
                .balanceOption(option)
                .build();

        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(balanceOptionRepository.findById(any())).thenReturn(Optional.of(option));
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(voteRepository.save(any())).thenReturn(vote);

        VoteRequest voteRequest = VoteRequest.builder()
                .selectedOptionId(option.getId())
                .memberId(member.getId())
                .build();

        // when
        Vote createdVote = voteService.createVote(post.getId(), voteRequest);

        // then
        assertThat(createdVote.getMember()).isEqualTo(member);
        assertThat(createdVote.getBalanceOption()).isEqualTo(option);
    }

    @Test
    @DisplayName("각 선택지의 제목과 투표 수를 조회한다.")
    void readVotingStatus_Success() {
        // given
        List<Vote> votesForA = new ArrayList<>();
        for (long i = 0; i < 5; i++) {
            votesForA.add(createVote(i));
        }
        List<Vote> votesForB = new ArrayList<>();
        for (long i = 0; i < 3; i++) {
            votesForB.add(createVote(i));
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

    @Test
    @DisplayName("투표를 수정한다.")
    void updateVote_Success() {
        // given
        Post post = Post.builder()
                .id(1L)
                .category(PostCategory.DISCUSSION)
                .build();

        BalanceOption optionA = BalanceOption.builder()
                .id(1L)
                .title("A")
                .post(post)
                .build();
        BalanceOption optionB = BalanceOption.builder()
                .id(2L)
                .title("B")
                .post(post)
                .build();
        Vote oldVote = Vote.builder()
                .id(1L)
                .balanceOption(optionA)
                .build();
        List<Vote> votes = List.of(oldVote);

        Member member = Member.builder()
                .id(1L)
                .votes(votes)
                .build();

        Vote newVote = Vote.builder()
                .id(1L)
                .balanceOption(optionB)
                .build();

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(balanceOptionRepository.findById(optionB.getId())).thenReturn(Optional.of(optionB));

        VoteRequest voteRequest = VoteRequest.builder()
                .selectedOptionId(optionB.getId())
                .memberId(member.getId())
                .build();

        // when
        Vote result = voteService.updateVote(post.getId(), voteRequest);

        // then
        assertThat(result.getBalanceOption().getId()).isEqualTo(newVote.getBalanceOption().getId());
        assertThat(result.getBalanceOption().getTitle()).isEqualTo(newVote.getBalanceOption().getTitle());
    }

    private Vote createVote(Long id) {
        return Vote.builder()
                .id(id)
                .build();
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
                .votes(List.of())
                .build();
    }
}