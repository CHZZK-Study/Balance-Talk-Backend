package balancetalk.game.domain;

import balancetalk.global.common.BaseTimeEntity;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.vote.domain.VoteOption;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.stream.IntStream;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Game extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private GameSet gameSet;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameOption> gameOptions = new ArrayList<>();

    @NotBlank
    @Size(max = 100)
    private String description;

    private LocalDateTime editedAt;

    public long getVoteCount(VoteOption optionType) {
        GameOption option = gameOptions.stream()
                .filter(gameOption -> gameOption.isTypeEqual(optionType))
                .findFirst()
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_OPTION_VOTE));
        return option.getVotesCount();
    }

    public void assignGameSet(GameSet gameSet) {
        this.gameSet = gameSet;
    }


    public void updateGame(Game newGame) {
        this.description = newGame.getDescription();
        this.editedAt = LocalDateTime.now();

        IntStream.range(0, newGame.getGameOptions().size()).forEach(i -> {
            GameOption currentOption = this.gameOptions.get(i);
            GameOption newOption = newGame.getGameOptions().get(i);
            currentOption.updateGameOption(newOption);
        });
    }
}
