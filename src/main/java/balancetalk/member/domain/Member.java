package balancetalk.member.domain;

import balancetalk.bookmark.domain.TalkPickBookmark;
import balancetalk.bookmark.domain.GameBookmark;
import balancetalk.game.domain.Game;
import balancetalk.game.domain.GameSet;
import balancetalk.game.domain.TempGameSet;
import balancetalk.global.common.BaseTimeEntity;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.like.domain.Like;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.TempTalkPick;
import balancetalk.vote.domain.GameVote;
import balancetalk.vote.domain.TalkPickVote;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Size(min = 2, max = 30)
    private String nickname;

    @NotBlank
    @Size(max = 30)
    @Email(regexp = "^[a-zA-Z0-9._%+-]{1,20}@[a-zA-Z0-9.-]{1,10}\\.[a-zA-Z]{2,}$")
    private String email;

    @NotBlank
    private String password;

    private String username; // 소셜 로그인으로 가입했을 때 식별하기 위해 설정

    @Enumerated(value = EnumType.STRING)
    private Role role;

    private String profileImgUrl;

    @OneToMany(mappedBy = "member")
    private List<TalkPickVote> talkPickVotes = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<GameVote> gameVotes = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<TalkPickBookmark> talkPickBookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<GameBookmark> gameBookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<TalkPick> talkPicks = new ArrayList<>();

    @OneToOne(mappedBy = "member")
    private TempTalkPick tempTalkPick;

    @OneToMany(mappedBy = "member")
    private List<GameSet> gameSets = new ArrayList<>();

    @OneToOne(mappedBy = "member")
    private TempGameSet tempGameSet;

    @OneToMany(mappedBy = "member")
    private List<Like> likes = new ArrayList<>();

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public boolean hasBookmarked(Long resourceId, Long gameId) {
        return this.gameBookmarks.stream()
                .anyMatch(bookmark -> bookmark.matches(resourceId, gameId) && bookmark.isActive());
    }

    public boolean hasBookmarked(TalkPick talkPick) {
        return this.talkPickBookmarks.stream()
                .anyMatch(bookmark -> bookmark.matches(talkPick) && bookmark.isActive());
    }

    public Optional<TalkPickVote> getVoteOnTalkPick(TalkPick talkPick) {
        return this.talkPickVotes.stream()
                .filter(vote -> vote.matchesTalkPick(talkPick))
                .findAny();
    }

    public boolean hasVotedTalkPick(TalkPick talkPick) {
        return talkPickVotes.stream()
                .anyMatch(vote -> vote.matchesTalkPick(talkPick));
    }

    public Optional<GameVote> getVoteOnGame(Game game) {
        return gameVotes.stream()
                .filter(vote -> vote.getGameOption().getGame().equals(game))
                .findAny();
    }

    public Optional<GameVote> getVoteOnGameOption(Member member, Game game) {
        return member.getGameVotes().stream()
                .filter(vote -> game.getGameOptions().stream()
                        .anyMatch(gameOption -> vote.matchesGameOption(gameOption)))
                .findAny();
    }

    public boolean hasVotedGame(Game game) {
        return gameVotes.stream()
                .anyMatch(vote -> game.getGameOptions().stream()
                        .anyMatch(gameOption -> vote.matchesGameOption(gameOption)));
    }

    public boolean isMyTalkPick(TalkPick talkPick) {
        return talkPicks.contains(talkPick);
    }

    public boolean isMyGameSet(GameSet gameSet) {
        return gameSets.contains(gameSet);
    }

    public Optional<TalkPickBookmark> getTalkPickBookmarkOf(TalkPick talkPick) {
        return talkPickBookmarks.stream()
                .filter(bookmark -> bookmark.matches(talkPick))
                .findFirst();
    }

    public Optional<GameBookmark> getGameBookmarkOf(long resourceId) {
        return gameBookmarks.stream()
                .filter(bookmark -> bookmark.matches(resourceId))
                .findFirst();
    }

    public TalkPick getTalkPickById(long talkPickId) {
        return talkPicks.stream()
                .filter(talkPick -> talkPick.matchesId(talkPickId))
                .findFirst()
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_TALK_PICK_THAT_MEMBER));
    }

    public GameSet getGameSetById(long gameSetId) {
        return gameSets.stream()
                .filter(gameSet -> gameSet.matchesId(gameSetId))
                .findFirst()
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_BALANCE_GAME_SET));
    }

    public boolean hasTempTalkPick() {
        return tempTalkPick != null;
    }

    public boolean hasTempGameSet() {
        return tempGameSet != null;
    }

    public Long updateTempTalkPick(TempTalkPick newTempTalkPick) {
        return tempTalkPick.update(newTempTalkPick);
    }

    public Long updateTempGameSet(TempGameSet newTempGameSet) {
        return tempGameSet.updateTempGameSet(newTempGameSet);
    }

    public int getPostsCount() {
        return talkPicks.size() + gameSets.size();
    }

    public int getBookmarkedPostsCount() {
        return gameBookmarks.size();
    }
}
