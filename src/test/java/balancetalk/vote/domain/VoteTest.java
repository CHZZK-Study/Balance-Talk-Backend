package balancetalk.vote.domain;

import balancetalk.talkpick.domain.TalkPick;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static balancetalk.vote.domain.VoteOption.*;
import static org.assertj.core.api.Assertions.assertThat;

class VoteTest {

    @Test
    @DisplayName("투표 대상 톡픽이 전달 톡픽과 일치하면 성공한다.")
    void matchesTalkPick_True() {
        // given
        TalkPick talkPick = TalkPick.builder().build();
        TalkPickVote vote = TalkPickVote.builder().talkPick(talkPick).build();

        // when
        boolean result = vote.matchesTalkPick(talkPick);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("투표 선택지를 성공적으로 수정한다.")
    void updateVoteOption_Success() {
        // given
        TalkPickVote vote = TalkPickVote.builder().voteOption(A).build();

        // when
        vote.updateVoteOption(B);

        // then
        assertThat(vote.getVoteOption()).isEqualTo(B);
    }

    @Test
    @DisplayName("투표 선택지가 전달된 선택지와 일치하면 성공한다.")
    void isVoteOptionEquals_True() {
        // given
        TalkPickVote vote = TalkPickVote.builder().voteOption(A).build();

        // when
        boolean result = vote.isVoteOptionEquals(A);

        // then
        assertThat(result).isTrue();
    }
}