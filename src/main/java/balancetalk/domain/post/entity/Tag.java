package balancetalk.domain.post.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {

    @Id
    @GeneratedValue
    @Column(name = "tag_id")
    private Long id;

    // TODO 개선 필요
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinColumn(name = "post_id")
//    private List<Post> posts = new ArrayList<>();
}
