package balancetalk.game.domain;

import balancetalk.global.common.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
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
public class TempGame extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private TempGameSet tempGameSet;

    @OneToMany(mappedBy = "tempGame", cascade = CascadeType.ALL)
    private List<TempGameOption> tempGameOptions = new ArrayList<>();

    @NotBlank
    @Size(max = 100)
    private String description;

    public void assignTempGameSet(TempGameSet tempGameSet) {
        this.tempGameSet = tempGameSet;
    }

    public void updateTempGame(TempGame newTempGame) {
        this.description = newTempGame.getDescription();
        IntStream.range(0, newTempGame.getTempGameOptions().size()).forEach(i -> {
            TempGameOption currentOption = this.tempGameOptions.get(i);
            TempGameOption newOption = newTempGame.getTempGameOptions().get(i);
            currentOption.updateTempGameOption(newOption);
        });
    }

    public List<Long> getGameOptionIds() {
        return tempGameOptions.stream().map(TempGameOption::getId).toList();
    }
}
