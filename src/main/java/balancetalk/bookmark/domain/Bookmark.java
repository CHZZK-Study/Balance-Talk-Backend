package balancetalk.bookmark.domain;

import balancetalk.global.common.BaseTimeEntity;
import balancetalk.member.domain.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    private Long resourceId;

    private Long gameId;

    @NotNull
    private Boolean active;

    @NotNull
    private Boolean isEndGameSet;

    @NotNull
    @Enumerated(EnumType.STRING)
    private BookmarkType bookmarkType;

    public boolean matches(long resourceId, long gameId, BookmarkType bookmarkType) {
        return isEqualsResourceId(resourceId) && isEqualsGameId(gameId) && isEqualsType(bookmarkType);
    }

    public boolean matches(long resourceId, BookmarkType bookmarkType) {
        return isEqualsResourceId(resourceId) && isEqualsType(bookmarkType);
    }

    private boolean isEqualsResourceId(long resourceId) {
        return this.resourceId.equals(resourceId);
    }

    private boolean isEqualsGameId(long gameId) {
        return this.gameId.equals(gameId);
    }

    private boolean isEqualsType(BookmarkType bookmarkType) {
        return this.bookmarkType == bookmarkType;
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
