package balancetalk.bookmark.domain;

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

    @NotNull
    private Long gameSetId;

    @NotNull
    private Long gameId;

    @NotNull
    private Boolean active;

    @NotNull
    private Boolean isEndGameSet;

    public boolean matches(long gameSetId) {
        return isEqualsGameSetId(gameSetId);
    }

    public boolean matches(long resourceId, long gameId) {
        return isEqualsGameSetId(resourceId) && isEqualsGameId(gameId);
    }

    private boolean isEqualsGameSetId(long gameSetId) {
        return this.gameSetId.equals(gameSetId);
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
