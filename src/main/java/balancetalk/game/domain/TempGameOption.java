package balancetalk.game.domain;

import balancetalk.vote.domain.VoteOption;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
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
public class TempGameOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Size(max = 30)
    private String name;

    @Size(max = 50)
    private String description;

    @Enumerated(value = EnumType.STRING)
    private VoteOption optionType;

    private Long imgId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "temp_game_id")
    private TempGame tempGame;

    public void assignTempGame(TempGame tempGame) {
        this.tempGame = tempGame;
    }

    public void updateTempGameOption(TempGameOption newTempGameOption) {
        this.name = newTempGameOption.getName();
        this.description = newTempGameOption.getDescription();
        this.imgId = newTempGameOption.getImgId();
    }
}
