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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
public class TempGameSet extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToMany(mappedBy = "tempGameSet", cascade = CascadeType.ALL)
    private List<TempGame> tempGames = new ArrayList<>();

    @Size(max = 50)
    private String title;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void addGames(List<TempGame> tempGames) {
        this.tempGames = tempGames;
        tempGames.forEach(tempGame -> {
            tempGame.assignTempGameSet(this);
            tempGame.getTempGameOptions().forEach(option -> option.assignTempGame(tempGame));
        });
    }

    public void updateTempGameSet(String title, List<TempGame> newTempGames) {
        this.title = title;
        IntStream.range(0, this.tempGames.size()).forEach(i -> {
            TempGame existingGame = this.tempGames.get(i);
            TempGame newGame = newTempGames.get(i);
            existingGame.updateTempGame(newGame);
        });
    }

    public List<Long> getAllFileIds() {
        return tempGames.stream()
                .flatMap(game -> game.getTempGameOptions().stream())
                .map(TempGameOption::getImgId)
                .filter(Objects::nonNull)
                .toList();
    }
}