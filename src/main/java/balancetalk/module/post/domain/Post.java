package balancetalk.module.post.domain;

import balancetalk.global.common.BaseTimeEntity;
import balancetalk.module.ViewStatus;
import balancetalk.module.bookmark.domain.Bookmark;
import balancetalk.module.comment.domain.Comment;
import balancetalk.module.member.domain.Member;
import balancetalk.module.report.domain.Report;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Formula;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String title;

    @NotNull
    @Future
    @Column(nullable = false)
    private LocalDateTime deadline;

    @PositiveOrZero
    @ColumnDefault("0")
    @Column(nullable = false)
    private Long views;

    @Enumerated(value = EnumType.STRING)
    @ColumnDefault("'NORMAL'")
    @Column(nullable = false)
    private ViewStatus viewStatus;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private PostCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<BalanceOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostLike> likes = new ArrayList<>();

    @Formula("(select count(*) from post_like where post_like.post_id = post_id)")
    private long likesCount;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostTag> postTags = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Report> reports = new ArrayList<>();

    public boolean isCasual() {
        return this.category == PostCategory.CASUAL;
    }

    public long likesCount() {
        if (likes == null) {
            return 0;
        }
        return likes.size();
    }
    
    @PrePersist
    public void init() {
        this.views = 0L;
        this.viewStatus = ViewStatus.NORMAL;
    }

    public boolean notContainsBalanceOption(BalanceOption option) {
        return !options.contains(option);
    }

    public boolean hasDeadlineExpired() {
        return deadline.isBefore(LocalDateTime.now());
    }

    public void increaseViews() {
        views++;
    }
}
