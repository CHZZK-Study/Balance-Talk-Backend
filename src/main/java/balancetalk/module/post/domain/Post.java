package balancetalk.module.post.domain;

import balancetalk.module.comment.domain.Comment;
import balancetalk.module.report.domain.Report;
import balancetalk.module.ViewStatus;
import balancetalk.module.member.domain.Member;
import balancetalk.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @NotBlank
    @Max(50)
    private String title;

    @NotNull
    @Future
    private LocalDateTime deadline;

    @NotNull
    @PositiveOrZero
    private Long views;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private ViewStatus viewStatus;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private PostCategory Category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "post")
    private List<BalanceOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();


    @OneToMany(mappedBy = "post")
    private List<PostTag> postTags = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Report> reports = new ArrayList<>();
}
