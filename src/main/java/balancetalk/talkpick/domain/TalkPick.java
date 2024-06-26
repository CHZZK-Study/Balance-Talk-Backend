package balancetalk.talkpick.domain;

import balancetalk.file.domain.File;
import balancetalk.global.common.BaseTimeEntity;
import balancetalk.like.domain.Like;
import balancetalk.member.domain.Member;
import balancetalk.vote.domain.Vote;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TalkPick extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotBlank
    @Size(max = 255)
    private String title;

    @NotBlank
    @Size(max = 255)
    private String summary;

    @NotBlank
    @Size(max = 255)
    private String content;

    @NotBlank
    @Size(max = 50)
    @Column(name = "option_a")
    private String optionA;

    @NotBlank
    @Size(max = 50)
    @Column(name = "option_b")
    private String optionB;

    @PositiveOrZero
    private Long views = 0L;

    @Enumerated(value = EnumType.STRING)
    private ViewStatus viewStatus = ViewStatus.NORMAL;

    @OneToMany(mappedBy = "talkPick")
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "talkPick")
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "talkPick")
    private List<File> talkPickFiles = new ArrayList<>();
}
