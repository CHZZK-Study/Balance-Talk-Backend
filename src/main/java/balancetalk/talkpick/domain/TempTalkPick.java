package balancetalk.talkpick.domain;

import balancetalk.global.common.BaseTimeEntity;
import balancetalk.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TempTalkPick extends BaseTimeEntity {

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
