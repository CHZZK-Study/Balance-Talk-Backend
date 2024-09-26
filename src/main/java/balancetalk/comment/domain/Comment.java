package balancetalk.comment.domain;

import balancetalk.global.common.BaseTimeEntity;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.member.domain.Member;
import balancetalk.report.domain.Report;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.ViewStatus;
import balancetalk.vote.domain.VoteOption;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static balancetalk.global.exception.ErrorCode.FAIL_PARSE_NOTIFICATION_HISTORY;
import static balancetalk.global.exception.ErrorCode.FAIL_SERIALIZE_NOTIFICATION_HISTORY;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Comment extends BaseTimeEntity {

    private static final int MIN_COUNT_FOR_BLIND = 5;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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

    @Transient
    private Boolean isNotifiedForFirstReply = false;

    @Column(columnDefinition = "TEXT")
    private String notificationHistory;

    private LocalDateTime editedAt;

    public boolean isEdited() {
        return this.editedAt != null;
    }

    public void updateContent(String content) {
        this.content = content;
        this.editedAt = LocalDateTime.now();
    }

    public void setIsBest(boolean isBest) {
        this.isBest = isBest;
    }

    public boolean isBlind() {
        return this.viewStatus.equals(ViewStatus.BLIND);
    }

    public void incrementReportCount() {
        this.reportedCount++;

        if (this.reportedCount == MIN_COUNT_FOR_BLIND) {
            this.content = "신고된 댓글입니다.";
            this.viewStatus = ViewStatus.BLIND;
        }
    }

    public void setIsNotifiedForFirstReplyTrue() {
        this.isNotifiedForFirstReply = true;
    }

    // 알림 이력 조회
    public Map<String, Boolean> getNotificationHistory() {
        if (notificationHistory == null) {
            return new HashMap<>();
        }
        try {
            return OBJECT_MAPPER.readValue(notificationHistory, new TypeReference<Map<String, Boolean>>() {});
        } catch (IOException e) {
            throw new BalanceTalkException(FAIL_PARSE_NOTIFICATION_HISTORY);
        }
    }

    // 알림 이력 저장
    public void setNotificationHistory(Map<String, Boolean> history) {
        try {
            this.notificationHistory = OBJECT_MAPPER.writeValueAsString(history);
        } catch (IOException e) {
            throw new BalanceTalkException(FAIL_SERIALIZE_NOTIFICATION_HISTORY);
        }
    }
}