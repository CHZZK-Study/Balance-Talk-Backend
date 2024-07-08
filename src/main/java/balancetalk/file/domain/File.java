package balancetalk.file.domain;

import balancetalk.game.domain.Game;
import balancetalk.member.domain.Member;
import balancetalk.talkpick.domain.TalkPick;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talk_pick_id")
    private TalkPick talkPick;

    @NotNull
    @Positive
    private Long size;

    @NotBlank
    @Size(max = 50)
    private String uploadName;

    @Size(max = 50)
    private String storedName;

    @NotBlank
    @Size(max = 255)
    private String path;

    @Enumerated(value = EnumType.STRING)
    private FileType fileType;

    @Enumerated(value = EnumType.STRING)
    private FileFormat fileFormat;

    public String getUrl() {
        return path + storedName;
    }
}
