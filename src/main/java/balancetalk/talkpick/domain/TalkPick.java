package balancetalk.talkpick.domain;

import balancetalk.comment.domain.Comment;
import balancetalk.global.common.BaseTimeEntity;
import balancetalk.member.domain.Member;
import balancetalk.vote.domain.Vote;
import balancetalk.vote.domain.VoteOption;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TalkPick extends BaseTimeEntity {

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
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @NotBlank
    @Size(max = 10)
    @Column(name = "option_a")
    private String optionA;

    @NotBlank
    @Size(max = 10)
    @Column(name = "option_b")
    private String optionB;

    @PositiveOrZero
    @ColumnDefault("0")
    private Long views;

    @PositiveOrZero
    @ColumnDefault("0")
    private Long bookmarks;

    @Enumerated(value = EnumType.STRING)
    private ViewStatus viewStatus = ViewStatus.NORMAL;

    @OneToMany(mappedBy = "talkPick")
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "talkPick")
    private List<Comment> comments = new ArrayList<>();

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

    public void update(String title, String content, String optionA, String optionB) {
        this.title = title;
        this.content = content;
        this.optionA = optionA;
        this.optionB = optionB;
    }

    public boolean matchesId(long id) {
        return this.id == id;
    }
}
