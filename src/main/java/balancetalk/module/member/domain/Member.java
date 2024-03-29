package balancetalk.module.member.domain;

import balancetalk.module.bookmark.domain.Bookmark;
import balancetalk.module.comment.domain.Comment;
import balancetalk.module.comment.domain.CommentLike;
import balancetalk.module.file.domain.File;
import balancetalk.module.notice.domain.Notice;
import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostLike;
import balancetalk.module.report.domain.Report;
import balancetalk.module.vote.domain.Vote;
import balancetalk.global.common.BaseTimeEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotBlank
    @Size(min = 2, max = 10)
    @Column(nullable = false, length = 10, unique = true)
    private String nickname;

    @NotNull
    @Size(max = 30)
    @Email(regexp = "^[a-zA-Z0-9._%+-]{1,20}@[a-zA-Z0-9.-]{1,10}\\.[a-zA-Z]{2,}$")
    @Column(nullable = false, length = 30, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Role role;

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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id")
    private File profilePhoto;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateImage(File profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public int getPostCount() {
        return Optional.ofNullable(posts)
                .map(List::size).orElse(0);
    }

    public int getPostLikes() {
        return Optional.ofNullable(postLikes)
                .map(List::size).orElse(0);
    }

    public boolean hasVoted(Post post) {
        return votes.stream()
                .anyMatch(vote -> vote.getBalanceOption().getPost().equals(post));
    }

    public boolean hasBookmarked(Post post) {
        return bookmarks.stream()
                .anyMatch(bookmark -> bookmark.getPost().equals(post));
    }

    public boolean hasLiked(Post post) {
        return postLikes.stream()
                .anyMatch(like -> like.getPost().equals(post));
    }

    public boolean hasLikedComment(Comment comment) {
        return commentLikes.stream()
                .anyMatch(like -> like.getComment().equals(comment));
    }
}
