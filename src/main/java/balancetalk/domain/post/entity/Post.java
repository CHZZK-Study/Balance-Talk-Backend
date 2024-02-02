package balancetalk.domain.post.entity;

import balancetalk.domain.comment.entity.Comment;
import balancetalk.domain.report.entity.Report;
import balancetalk.domain.ViewStatus;
import balancetalk.domain.member.entity.Member;
import balancetalk.domain.post.enums.PostCategory;
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

    // TODO 개선 필요
//    @ManyToMany(mappedBy = "tag")
//    @JoinColumn(name = "tag_id")
//    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Report> reports = new ArrayList<>();
}
