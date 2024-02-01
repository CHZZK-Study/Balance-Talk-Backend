package balancetalk.domain.member.entity;

import balancetalk.domain.post.entity.Bookmark;
import balancetalk.domain.comment.entity.Comment;
import balancetalk.domain.comment.entity.CommentLike;
import balancetalk.domain.Notice;
import balancetalk.domain.post.entity.Post;
import balancetalk.domain.post.entity.PostLike;
import balancetalk.domain.report.entity.Report;
import balancetalk.domain.vote.entity.Vote;
import balancetalk.domain.member.enums.Role;
import balancetalk.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotNull
    @Size(min = 2, max = 10)
    private String nickname;

    @NotNull
    @Size(max = 30)
    @Email
    // todo: email pattern
    private String email;

    @NotNull
    @Size(max = 20)
    // todo: password pattern
    private String password;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private Role role;

    @Min(15)
    private String ip;

    @OneToMany(mappedBy = "member")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<CommentLike> commentLikes = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Notice> notices = new ArrayList<>();

    @OneToMany(mappedBy = "reporter")
    private List<Report> reports = new ArrayList<>(); // 신고한 기록
}
