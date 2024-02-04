package balancetalk.module.post.domain;

<<<<<<< HEAD
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

=======
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
>>>>>>> 547f39e6a5cb7e6f2afde22a7d454ef82dee714e
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {

    @Id
    @GeneratedValue
    @Column(name = "tag_id")
    private Long id;

<<<<<<< HEAD
    // TODO 개선 필요
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinColumn(name = "post_id")
//    private List<Post> posts = new ArrayList<>();
=======
    @NotNull
    private String name;

    @OneToMany(mappedBy = "tag")
    private List<PostTag> postTags = new ArrayList<>();
>>>>>>> 547f39e6a5cb7e6f2afde22a7d454ef82dee714e
}
