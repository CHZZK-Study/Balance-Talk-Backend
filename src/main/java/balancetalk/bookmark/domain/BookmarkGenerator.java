package balancetalk.bookmark.domain;

import balancetalk.game.domain.GameSet;
import balancetalk.member.domain.Member;
import balancetalk.talkpick.domain.TalkPick;
import org.springframework.stereotype.Component;

@Component
public class BookmarkGenerator {

    public TalkPickBookmark generate(final TalkPick talkPick, final Member member) {
        return TalkPickBookmark.builder()
                .member(member)
                .talkPick(talkPick)
                .active(true)
                .build();
    }

    public GameBookmark generate(final GameSet gameSet, final long gameId, final Member member) {
        return GameBookmark.builder()
                .member(member)
                .gameSet(gameSet)
                .gameId(gameId)
                .active(true)
                .isEndGameSet(false)
                .build();
    }
}
