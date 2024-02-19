package balancetalk.module.post.domain;

import balancetalk.module.file.domain.File;
import balancetalk.module.vote.domain.Vote;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BalanceOption {

    @Id
    @GeneratedValue
    @Column(name = "balance_option_id")
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String title;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String description;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id")
    private File file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany(mappedBy = "balanceOption")
    private List<Vote> votes = new ArrayList<>();

    public int voteCount() {
        return votes.size();
    }
  
    public void addPost(Post post) {
        this.post = post;
    }
}
