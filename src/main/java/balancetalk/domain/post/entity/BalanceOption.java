package balancetalk.domain.post.entity;

import balancetalk.domain.file.entity.File;
import balancetalk.domain.vote.entity.Vote;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BalanceOption {

    @Id
    @GeneratedValue
    @Column(name = "balance_option_id")
    private Long id;

    @NotNull
    @Max(50)
    private String title;

    @NotNull
    @Max(100)
    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private File file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany(mappedBy = "balanceOption")
    private List<Vote> votes = new ArrayList<>();
}
