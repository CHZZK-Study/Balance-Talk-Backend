package balancetalk.vote.domain;

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
