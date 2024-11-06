package balancetalk.talkpick.domain;

import balancetalk.member.domain.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TempTalkPick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Size(max = 50)
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Size(max = 10)
    @Column(name = "option_a")
    private String optionA;

    @Size(max = 10)
    @Column(name = "option_b")
    private String optionB;

    private String sourceUrl;

    public Long update(TempTalkPick newTempTalkPick) {
        this.title = newTempTalkPick.getTitle();
        this.content = newTempTalkPick.getContent();
        this.optionA = newTempTalkPick.getOptionA();
        this.optionB = newTempTalkPick.getOptionB();
        this.sourceUrl = newTempTalkPick.getSourceUrl();
        return id;
    }
}
