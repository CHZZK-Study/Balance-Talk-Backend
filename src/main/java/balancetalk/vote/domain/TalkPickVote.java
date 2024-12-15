package balancetalk.vote.domain;

import balancetalk.global.common.BaseTimeEntity;
import balancetalk.member.domain.Member;
import balancetalk.talkpick.domain.TalkPick;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TalkPickVote extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talk_pick_id")
    private TalkPick talkPick;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private VoteOption voteOption;

    public boolean matchesTalkPick(TalkPick talkPick) {
        return this.talkPick.equals(talkPick);
    }

    public boolean isVoteOptionEquals(VoteOption voteOption) {
        return this.voteOption.equals(voteOption);
    }

    public void updateVoteOption(VoteOption newVoteOption) {
        this.voteOption = newVoteOption;
    }
}
