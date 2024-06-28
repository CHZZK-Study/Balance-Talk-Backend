package balancetalk.game.domain;

import balancetalk.module.file.domain.FileType;
import balancetalk.module.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GameFile {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Long size;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String uploadName;

    @Size(max = 50)
    @Column(unique = true, nullable = false)
    private String storedName;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String path;

    @NotBlank
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private FileType type;
}
