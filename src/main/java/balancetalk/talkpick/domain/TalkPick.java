package balancetalk.talkpick.domain;

import static balancetalk.global.exception.ErrorCode.FAIL_PARSE_NOTIFICATION_HISTORY;
import static balancetalk.global.exception.ErrorCode.FAIL_SERIALIZE_NOTIFICATION_HISTORY;

import balancetalk.comment.domain.Comment;
import balancetalk.global.common.BaseTimeEntity;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.member.domain.Member;
import balancetalk.vote.domain.TalkPickVote;
import balancetalk.vote.domain.VoteOption;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TalkPick extends BaseTimeEntity {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotBlank
    @Size(max = 50)
    private String title;

    @Embedded
    private Summary summary;

    @NotBlank
    @Size(max = 2000)
    private String content;

    @NotBlank
    @Size(max = 10)
    @Column(name = "option_a")
    private String optionA;

    @NotBlank
    @Size(max = 10)
    @Column(name = "option_b")
    private String optionB;

    private String sourceUrl;

    @PositiveOrZero
    @ColumnDefault("0")
    private Long views;

    @PositiveOrZero
    @ColumnDefault("0")
    private Long bookmarks;

    private LocalDateTime editedAt;

    @Enumerated(value = EnumType.STRING)
    private ViewStatus viewStatus = ViewStatus.NORMAL;

    @OneToMany(mappedBy = "talkPick")
    private List<TalkPickVote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "talkPick")
    private List<Comment> comments = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String notificationHistory;

    public void increaseViews() {
        this.views++;
    }

    public long votesCountOf(VoteOption voteOption) {
        return votes.stream()
                .filter(vote -> vote.isVoteOptionEquals(voteOption))
                .count();
    }

    public String getWriterNickname() {
        return this.member.getNickname();
    }

    public void increaseBookmarks() {
        this.bookmarks++;
    }

    public void decreaseBookmarks() {
        this.bookmarks--;
    }

    public void update(TalkPick newTalkPick) {
        this.title = newTalkPick.getTitle();
        this.content = newTalkPick.getContent();
        this.optionA = newTalkPick.getOptionA();
        this.optionB = newTalkPick.getOptionB();
        this.sourceUrl = newTalkPick.getSourceUrl();
        this.editedAt = LocalDateTime.now();
    }

    public boolean matchesId(long id) {
        return this.id == id;
    }

    public boolean isEdited() {
        return editedAt != null;
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

    public void updateSummary(Summary newSummary) {
        this.summary = newSummary;
    }
}
