package balancetalk.domain;

import balancetalk.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String nickname;
    private String email;
    private String password;
    private Role role;
    private String ip;

    @OneToMany(mappedBy = "post")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "post_like")
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "bookmark")
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "comment")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "comment_like")
    private List<CommentLike> commentLikes = new ArrayList<>();

    @OneToMany(mappedBy = "vote")
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "notice")
    private List<Notice> notices = new ArrayList<>();

    @OneToMany(mappedBy = "report")
    private List<Report> reports = new ArrayList<>(); // 신고한 기록

    @OneToMany(mappedBy = "report")
    private List<Report> reported = new ArrayList<>(); // 신고 받은 기록
}
