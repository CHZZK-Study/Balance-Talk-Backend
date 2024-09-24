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
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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
    @Size(max = 50)
    private String title;

    @NotBlank
    @Size(max = 100)
    private String description;

    @PositiveOrZero
    @ColumnDefault("0")
    private Long bookmarks;

    public void addTempGameSet(TempGameSet tempGameSet) {
        this.tempGameSet = tempGameSet;
    }

    public void updateTempGame(TempGame newTempGame) {
        this.title = newTempGame.getTitle();
        this.description = newTempGame.getDescription();
        List<TempGameOption> newTempOptions = newTempGame.getTempGameOptions();
        newTempOptions.forEach(newOption -> {
            this.tempGameOptions.stream()
                    .filter(option -> option.getId().equals(newOption.getId()))
                    .findFirst()
                    .ifPresentOrElse(
                            option -> option.update(newOption),
                            () -> this.tempGameOptions.add(newOption)
                    );
        });
    }
}
