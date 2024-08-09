package balancetalk.game.domain;

import balancetalk.global.common.BaseTimeEntity;
import balancetalk.member.domain.Member;
import balancetalk.vote.domain.Vote;
import balancetalk.vote.domain.VoteOption;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Game extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_topic_id")
    private GameTopic gameTopic;

    @NotBlank
    @Size(max = 255)
    private String title;

    @NotBlank
    @Size(max = 50)
    private String optionA;

    @NotBlank
    @Size(max = 50)
    private String optionB;

    private String optionAImg;

    private String optionBImg;

    private LocalDateTime editedAt;

    @PositiveOrZero
    private long views = 0L;

    @OneToMany(mappedBy = "game")
    private List<Vote> votes = new ArrayList<>();

    public void increaseViews() {
        this.views++;
    }

    public long getVoteCounts(VoteOption voteOption) {
        return votes.stream()
                .filter(vote -> vote.isVoteOptionEquals(voteOption))
                .count();
    }

    public void edit() { // 밸런스 게임 수정 시 호출
        // this.title = title;
        // this.optionA = optionA;
        // this.optionB = optionB;
        this.editedAt = LocalDateTime.now();
    }
}
