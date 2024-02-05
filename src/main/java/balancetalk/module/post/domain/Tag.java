package balancetalk.module.post.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {

    @Id
    @GeneratedValue
    @Column(name = "tag_id")
    private Long id;

    @NotNull
    private String name;

    @OneToMany(mappedBy = "tag")
    private List<PostTag> postTags = new ArrayList<>();

    public Tag(String name){
        this.name = name;
    }
}
