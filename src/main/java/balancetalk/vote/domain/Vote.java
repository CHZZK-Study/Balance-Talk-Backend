package balancetalk.vote.domain;

import balancetalk.global.common.BaseTimeEntity;
import balancetalk.member.domain.Member;
import balancetalk.talkpick.domain.TalkPick;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotBlank
    @Enumerated(value = EnumType.STRING)
    private VoteOption voteOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talk_pick_id")
    private TalkPick talkPick;
}
