package balancetalk.game.domain;

import balancetalk.global.common.BaseTimeEntity;
import balancetalk.member.domain.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
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
public class TempGameSet extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToMany(mappedBy = "tempGameSet", cascade = CascadeType.ALL)
    private List<TempGame> tempGames = new ArrayList<>();

    @NotBlank
    @Size(max = 50)
    private String title;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_tag_id")
    private MainTag mainTag;

    @PositiveOrZero
    @ColumnDefault("0")
    private long views;

    @Size(max = 10)
    private String subTag;

    @PositiveOrZero
    @ColumnDefault("0")
    private Long bookmarks;

    private LocalDateTime editedAt;

    public void addTempGames(List<TempGame> tempGames) {
        this.tempGames = tempGames;
        tempGames.forEach(tempGame -> {
            tempGame.assignTempGameSet(this);
            tempGame.getTempGameOptions().forEach(tempGameOption -> tempGameOption.addTempGame(tempGame));
        });
    }


    public TempGameSet updateTempGameSet(TempGameSet newTempGameSet) {
        this.mainTag = newTempGameSet.getMainTag();
        this.subTag = newTempGameSet.getSubTag();

        List<TempGame> newTempGames = newTempGameSet.getTempGames();

        newTempGames.forEach(newGame -> {
            this.tempGames.stream()
                    .filter(existingGame -> existingGame.getId().equals(newGame.getId()))
                    .findFirst()
                    .ifPresentOrElse(
                            existingGame -> existingGame.updateTempGame(newGame),
                            () -> {
                                this.tempGames.add(newGame);
                                newGame.assignTempGameSet(this);
                            }
                    );
        });
        return this;
    }
}