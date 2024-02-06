package balancetalk.module.vote.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.post.domain.BalanceOption;
import balancetalk.module.post.domain.BalanceOptionRepository;
import balancetalk.module.vote.domain.Vote;
import balancetalk.module.vote.domain.VoteRepository;
import balancetalk.module.vote.dto.VoteRequest;
import java.util.Optional;
import org.assertj.core.api.Assertions;
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

    @Test
    @DisplayName("회원과 선택지 정보를 바탕으로 투표를 생성한다.")
    void createVote_Success() {
        // given
        BalanceOption option = BalanceOption.builder()
                .id(1L)
                .title("A")
                .description("aaa")
                .build();

        Member member = Member.builder()
                .id(1L)
                .email("hhh2222@gmail.com")
                .nickname("testMember")
                .password("password123@")
                .build();

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
        Assertions.assertThat(createdVote.getMember()).isEqualTo(member);
        Assertions.assertThat(createdVote.getBalanceOption()).isEqualTo(option);
    }
}