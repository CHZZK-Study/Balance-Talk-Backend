package balancetalk.game.domain;

import balancetalk.global.common.BaseTimeEntity;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
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
public class GameSet extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToMany(mappedBy = "gameSet", cascade = CascadeType.ALL)
    private List<Game> games = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
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

    public void increaseViews() {
        this.views++;
    }

    public boolean matchesId(long id) {
        return this.id == id;
    }

    public Game getGameById(long id) {
        return games.stream()
                .filter(game -> game.matchesId(id))
                .findFirst()
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_BALANCE_GAME));

    public void increaseBookmarks() {
        this.bookmarks++;
    }

    public void decreaseBookmarks() {
        this.bookmarks--;
    }

    public boolean hasGame(Long gameId) {
        return games.stream().anyMatch(game -> game.getId().equals(gameId));
    }

    public void addGames(List<Game> games) {
        this.games = games;
        games.forEach(game -> {
            game.addGameSet(this);
            game.getGameOptions().forEach(option -> option.addGame(game));
        });
    }
}
