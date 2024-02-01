package balancetalk.domain;

import balancetalk.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    private String title;
    private LocalDateTime deadline;
    private Long views;

    @Enumerated(value = EnumType.STRING)
    private ViewStatus viewStatus;

    @Enumerated(value = EnumType.STRING)
    private PostCategory Category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "balance_option")
    private List<BalanceOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "post_like")
    private List<PostLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "comment")
    private List<Comment> comments = new ArrayList<>();

    // TODO 개선 필요
//    @ManyToMany(mappedBy = "tag")
//    @JoinColumn(name = "tag_id")
//    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "bookmark")
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "report")
    private List<Report> reports = new ArrayList<>();
}
