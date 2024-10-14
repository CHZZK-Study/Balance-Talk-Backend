package balancetalk.game.domain;

import static balancetalk.global.exception.ErrorCode.FAIL_PARSE_NOTIFICATION_HISTORY;
import static balancetalk.global.exception.ErrorCode.FAIL_SERIALIZE_NOTIFICATION_HISTORY;

import balancetalk.global.common.BaseTimeEntity;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.vote.domain.VoteOption;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<GameOption> gameOptions = new ArrayList<>();

    @NotBlank
    @Size(max = 50)
    private String title;

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

    public void addGameSet(GameSet gameSet) {
        this.gameSet = gameSet;
    }

    public boolean matchesId(long id) {
        return this.id == id;
    }

    public void updateGame(Game newGame) {
        this.title = newGame.getTitle();
        this.description = newGame.getDescription();
        this.editedAt = LocalDateTime.now();
        IntStream.range(0, this.gameOptions.size())
                .forEach(i -> this.gameOptions.get(i).updateOption(newGame.getGameOptions().get(i)));
    }
}
