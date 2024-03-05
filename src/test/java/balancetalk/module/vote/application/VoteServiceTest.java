package balancetalk.module.vote.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    private final String AUTHENTICATED_EMAIL = "user@example.com";

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

    @BeforeEach
    void setUp() {
        // SecurityContext에 인증된 사용자 설정
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        lenient().when(authentication.getName()).thenReturn(AUTHENTICATED_EMAIL);
    }

    @Test
    @DisplayName("회원과 선택지 정보를 바탕으로 투표를 생성한다.")
    void createVote_Success_withMember() {
        // given
        BalanceOption option = createBalanceOption(1L, "A", List.of());
        Post post = Post.builder()
                .id(1L)
                .deadline(LocalDateTime.now().plusDays(1))
                .options(List.of(option))
                .build();
        Member member = createMember();
        Vote vote = Vote.builder()
                .id(1L)
                .member(member)
                .balanceOption(option)
                .build();

        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(balanceOptionRepository.findById(any())).thenReturn(Optional.of(option));
        when(memberRepository.findByEmail(AUTHENTICATED_EMAIL)).thenReturn(Optional.of(member));
        when(voteRepository.save(any())).thenReturn(vote);

        VoteRequest voteRequest = VoteRequest.builder()
                .selectedOptionId(option.getId())
                .isUser(true)
                .build();

        // when
        Vote createdVote = voteService.createVote(post.getId(), voteRequest);

        // then
        assertThat(createdVote.getMember()).isEqualTo(member);
        assertThat(createdVote.getBalanceOption()).isEqualTo(option);
    }

    @Test
    @DisplayName("비회원과 선택지 정보를 바탕으로 투표를 생성한다.")
    void createVote_Success_withGuest() {
        // given
        BalanceOption option = createBalanceOption(1L, "A", List.of());
        Post post = Post.builder()
                .id(1L)
                .deadline(LocalDateTime.now().plusDays(1))
                .options(List.of(option))
                .build();
        Vote vote = Vote.builder()
                .id(1L)
                .balanceOption(option)
                .build();

        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(balanceOptionRepository.findById(any())).thenReturn(Optional.of(option));
        when(voteRepository.save(any())).thenReturn(vote);

        VoteRequest voteRequest = VoteRequest.builder()
                .selectedOptionId(option.getId())
                .isUser(false)
                .build();

        // when
        Vote createdVote = voteService.createVote(post.getId(), voteRequest);

        // then
        assertThat(createdVote.getBalanceOption()).isEqualTo(option);
    }

    @Test
    @DisplayName("투표 생성 시 게시글 정보가 없는 경우 예외가 발생한다.")
    void createVote_Fail_ByNotFoundPost() {
        // given
        when(postRepository.findById(any())).thenThrow(new BalanceTalkException(ErrorCode.NOT_FOUND_POST));

        // when, then
        assertThatThrownBy(()-> voteService.createVote(1L, new VoteRequest(1L, true)))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_POST.getMessage());
    }

    @Test
    @DisplayName("투표 생성 시 선택지 정보가 없는 경우 예외가 발생한다.")
    void createVote_Fail_ByNotFoundBalanceOption() {
        // given
        Post post = Post.builder()
                .id(1L)
                .deadline(LocalDateTime.now().plusDays(1))
                .build();
        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(balanceOptionRepository.findById(any()))
                .thenThrow(new BalanceTalkException(ErrorCode.NOT_FOUND_BALANCE_OPTION));

        // when, then
        assertThatThrownBy(() -> voteService.createVote(1L, new VoteRequest(1L, true)))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_BALANCE_OPTION.getMessage());
    }

    @Test
    @DisplayName("투표 생성 시 회원 정보가 없는 경우 예외가 발생한다.")
    void createVote_Fail_ByNotFoundMember() {
        // given
        BalanceOption optionA = BalanceOption.builder().build();
        BalanceOption optionB = BalanceOption.builder().build();
        Post post = Post.builder()
                .id(1L)
                .deadline(LocalDateTime.now().plusDays(1))
                .options(List.of(optionA, optionB))
                .build();

        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(balanceOptionRepository.findById(any())).thenReturn(Optional.of(optionA));
        when(memberRepository.findByEmail(any())).thenThrow(new BalanceTalkException(ErrorCode.NOT_FOUND_MEMBER));

        // when, then
        assertThatThrownBy(() -> voteService.createVote(1L, new VoteRequest(1L, true)))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("투표 생성 시 선택지가 다른 게시글의 선택지인 경우 예외가 발생한다.")
    void createVote_Fail_ByPostNotContainsBalanceOption() {
        // given
        BalanceOption optionInOtherPost = BalanceOption.builder()
                .id(4L)
                .build();
        Post post = Post.builder()
                .id(1L)
                .deadline(LocalDateTime.now().plusDays(1))
                .options(List.of())
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(balanceOptionRepository.findById(4L)).thenReturn(Optional.of(optionInOtherPost));

        // when, then
        assertThatThrownBy(() -> voteService.createVote(1L, new VoteRequest(4L, false)))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining(ErrorCode.MISMATCHED_BALANCE_OPTION.getMessage());
    }

    @Test
    @DisplayName("투표 생성 시 게시글 마감 기한이 만료된 경우 예외가 발생한다.")
    void createVote_Fail_ByExpiredDeadline() {
        // given
        Post post = Post.builder()
                .id(1L)
                .deadline(LocalDateTime.now().minusDays(1))
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // when, then
        assertThatThrownBy(()-> voteService.createVote(1L, any()))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining(ErrorCode.EXPIRED_POST_DEADLINE.getMessage());
    }

    @Test
    @DisplayName("투표 생성 시 이미 투표한 기록이 있는 게시글인 경우 예외가 발생한다.")
    void createVote_Fail_ByAlreadyVoted() {
        // given
        BalanceOption balanceOption = BalanceOption.builder()
                .id(1L)
                .build();
        Post post = Post.builder()
                .id(1L)
                .deadline(LocalDateTime.now().plusDays(1))
                .options(List.of(balanceOption))
                .build();
        balanceOption.addPost(post);
        Vote vote = Vote.builder()
                .id(1L)
                .balanceOption(balanceOption)
                .build();
        Member member = Member.builder()
                .votes(List.of(vote))
                .build();

        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(balanceOptionRepository.findById(any())).thenReturn(Optional.of(balanceOption));
        when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));

        // when, then
        assertThatThrownBy(() -> voteService.createVote(1L, new VoteRequest(1L, true)))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining(ErrorCode.ALREADY_VOTE.getMessage());
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
        List<VotingStatusResponse> votingStatusResponses = voteService.votingStatus(1L);

        // then
        assertThat(votingStatusResponses.get(0).getOptionTitle()).isEqualTo("A");
        assertThat(votingStatusResponses.get(0).getVoteCount()).isEqualTo(5);
        assertThat(votingStatusResponses.get(1).getOptionTitle()).isEqualTo("B");
        assertThat(votingStatusResponses.get(1).getVoteCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("투표 현황 조회 시 게시글 정보가 없는 경우 예외를 발생시킨다.")
    void readVotingStatus_Fail_ByNotFoundPost() {
        // given
        when(postRepository.findById(any())).thenThrow(new BalanceTalkException(ErrorCode.NOT_FOUND_POST));

        // when, then
        assertThatThrownBy(() -> voteService.votingStatus(1L))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_POST.getMessage());
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
                .email(AUTHENTICATED_EMAIL)
                .votes(votes)
                .build();

        Vote newVote = Vote.builder()
                .id(1L)
                .balanceOption(optionB)
                .build();

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(memberRepository.findByEmail(AUTHENTICATED_EMAIL)).thenReturn(Optional.of(member));
        when(balanceOptionRepository.findById(optionB.getId())).thenReturn(Optional.of(optionB));

        VoteRequest voteRequest = VoteRequest.builder()
                .selectedOptionId(optionB.getId())
                .build();

        // when
        Vote result = voteService.updateVote(post.getId(), voteRequest);

        // then
        assertThat(result.getBalanceOption().getId()).isEqualTo(newVote.getBalanceOption().getId());
        assertThat(result.getBalanceOption().getTitle()).isEqualTo(newVote.getBalanceOption().getTitle());
    }

    @Test
    @DisplayName("투표 수정 시 게시글 정보가 없는 경우 예외를 발생시킨다.")
    void updateVote_Fail_ByNotFoundPost() {
        // when, then
        assertThatThrownBy(() -> voteService.updateVote(1L, new VoteRequest(1L, true)))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_POST.getMessage());
    }

    @Test
    @DisplayName("투표 수정 시 선택지 정보가 없는 경우 예외를 발생시킨다.")
    void updateVote_Fail_ByNotFoundBalanceOption() {
        // given
        Post post = Post.builder()
                .id(1L)
                .category(PostCategory.DISCUSSION)
                .build();

        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(balanceOptionRepository.findById(any()))
                .thenThrow(new BalanceTalkException(ErrorCode.NOT_FOUND_BALANCE_OPTION));

        // when, then
        assertThatThrownBy(() -> voteService.updateVote(1L, new VoteRequest(1L, true)))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining(ErrorCode.NOT_FOUND_BALANCE_OPTION.getMessage());
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

    private Member createMember() {
        return Member.builder()
                .id(1L)
                .email(AUTHENTICATED_EMAIL)
                .votes(List.of())
                .build();
    }
}