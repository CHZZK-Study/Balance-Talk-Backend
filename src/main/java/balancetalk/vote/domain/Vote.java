package balancetalk.vote.domain;

import balancetalk.game.domain.GameOption;
import balancetalk.global.common.BaseTimeEntity;
import balancetalk.member.domain.Member;
import balancetalk.talkpick.domain.TalkPick;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talk_pick_id")
    private TalkPick talkPick;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_option_id")
    private GameOption gameOption;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private VoteOption voteOption;

    public boolean matchesTalkPick(TalkPick talkPick) {
        return this.talkPick.equals(talkPick);
    }

    public boolean matchesGameOption(GameOption gameOption) {
        return this.gameOption.equals(gameOption);
    }

    public void updateVoteOption(VoteOption newVoteOption) {
        this.voteOption = newVoteOption;
    }

    public void updateGameOption(GameOption gameOption) {
        this.gameOption = gameOption;
    }

    public boolean isVoteOptionEquals(VoteOption voteOption) {
        return this.voteOption.equals(voteOption);
    }
}
