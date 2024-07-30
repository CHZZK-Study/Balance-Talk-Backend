package balancetalk.member.domain;

import balancetalk.bookmark.domain.Bookmark;
import balancetalk.bookmark.domain.BookmarkType;
import balancetalk.game.domain.Game;
import balancetalk.global.common.BaseTimeEntity;
import balancetalk.like.domain.Like;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.vote.domain.Vote;
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
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<TalkPick> talkPicks = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Game> games = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Like> likes = new ArrayList<>();

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public boolean hasBookmarked(Long resourceId, BookmarkType bookmarkType) {
        return this.bookmarks.stream()
                .anyMatch(bookmark -> bookmark.matches(resourceId, bookmarkType));
    }

    public Optional<Vote> getVoteOnTalkPick(TalkPick talkPick) {
        return this.votes.stream()
                .filter(vote -> vote.matchesTalkPick(talkPick))
                .findAny();
    }

    public boolean hasVotedTalkPick(TalkPick talkPick) {
        return votes.stream()
                .anyMatch(vote -> vote.matchesTalkPick(talkPick));
    }

    public Optional<Vote> getVoteOnGame(Game game) {
        return this.votes.stream()
                .filter(vote -> vote.matchesGame(game))
                .findAny();
    }

    public boolean hasVotedGame(Game game) {
        return votes.stream()
                .anyMatch(vote -> vote.matchesGame(game));
    }
}
