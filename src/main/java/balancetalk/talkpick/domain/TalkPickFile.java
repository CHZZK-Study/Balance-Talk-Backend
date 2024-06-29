package balancetalk.talkpick.domain;

import balancetalk.global.common.BaseTimeEntity;
import balancetalk.module.file.domain.FileType;
import balancetalk.module.member.domain.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TalkPickFile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talk_pick_id")
    private TalkPick talkPick;

    @NotNull
    @Positive
    private Long size;

    @NotBlank
    @Size(max = 50)
    private String uploadName;

    @NotBlank
    @Size(max = 50)
    private String storedName;

    @NotBlank
    @Size(max = 255)
    private String path;

    @NotBlank
    @Enumerated(value = EnumType.STRING)
    private FileType type;
}
