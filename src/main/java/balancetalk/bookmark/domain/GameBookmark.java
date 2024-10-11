package balancetalk.bookmark.domain;

import balancetalk.game.domain.GameSet;
import balancetalk.global.common.BaseTimeEntity;
import balancetalk.member.domain.Member;
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
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameBookmark extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_set_id")
    private GameSet gameSet;

    @NotNull
    private Long gameId;

    @NotNull
    private Boolean active;

    @NotNull
    private Boolean isEndGameSet;

    public boolean matches(GameSet gameSet) {
        return isEqualsGameSetId(gameSet);
    }

    public boolean matches(GameSet gameSet, long gameId) {
        return isEqualsGameSetId(gameSet) && isEqualsGameId(gameId);
    }

    private boolean isEqualsGameSetId(GameSet gameSet) {
        return this.gameSet.equals(gameSet);
    }

    private boolean isEqualsGameId(long gameId) {
        return this.gameId.equals(gameId);
    }

    public boolean isActive() {
        return active;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void updateGameId(Long gameId) {
        this.gameId = gameId;
    }

    public void setIsEndGameSet(boolean isEndGameSet) {
        this.isEndGameSet = isEndGameSet;
    }
}
