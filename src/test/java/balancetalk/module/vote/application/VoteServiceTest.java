package balancetalk.module.vote.application;

import balancetalk.module.member.domain.Member;
import balancetalk.module.post.domain.BalanceOption;
import balancetalk.module.vote.domain.Vote;
import balancetalk.module.vote.dto.VoteRequest;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class VoteServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    VoteService voteService;

    @Test
    @DisplayName("회원과 선택지 정보를 바탕으로 투표를 생성한다.")
    void createVote_Success() {
        // given
        Member member = Member.builder()
                .email("hhh2222@gmail.com")
                .nickname("testMember")
                .password("password123@")
                .build();
        em.persist(member);

        BalanceOption optionA = BalanceOption.builder()
                .title("A")
                .description("aaa")
                .build();
        BalanceOption optionB = BalanceOption.builder()
                .title("B")
                .description("bbb")
                .build();
        em.persist(optionA);
        em.persist(optionB);

        // when
        Vote vote = voteService.createVote(new VoteRequest(member.getId(), optionA.getId()));

        // then
        Assertions.assertThat(vote.getMember()).isEqualTo(member);
        Assertions.assertThat(vote.getBalanceOption()).isEqualTo(optionA);
    }
}