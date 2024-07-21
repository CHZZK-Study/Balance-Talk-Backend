package balancetalk.comment.domain;

import balancetalk.global.common.BaseTimeEntity;
import balancetalk.member.domain.Member;
import balancetalk.report.domain.Report;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.ViewStatus;
import balancetalk.vote.domain.VoteOption;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Comment extends BaseTimeEntity {

    private static final int MIN_COUNT_FOR_BLIND = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String content;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private VoteOption voteOption;

    @Enumerated(value = EnumType.STRING)
    private ViewStatus viewStatus;

    private Boolean isBest;

    private Boolean isBlind;

    @NotNull
    private int reportedCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talk_pick_id")
    private TalkPick talkPick;

    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Comment> replies = new ArrayList<>();

    public void updateContent(String content) {
        this.content = content;
    }

    public void setIsBest(boolean isBest) {
        this.isBest = isBest;
    }

    public void incrementReportCount() { // TODO : 옵저버 패턴 도입 해도 좋을지도
        this.reportedCount++;

        if (this.reportedCount >= MIN_COUNT_FOR_BLIND) {
            this.content = "신고된 댓글입니다.";
            this.isBlind = true;
        }
    }
}