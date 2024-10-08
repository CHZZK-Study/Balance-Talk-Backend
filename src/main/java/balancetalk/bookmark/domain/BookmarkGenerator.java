package balancetalk.bookmark.domain;

import balancetalk.member.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class BookmarkGenerator {

    public TalkPickBookmark generate(final long talkPickId, final Member member) {
        return TalkPickBookmark.builder()
                .member(member)
                .talkPickId(talkPickId)
                .active(true)
                .build();
    }

    public GameBookmark generate(final long gameSetId, final long gameId, final Member member) {
        return GameBookmark.builder()
                .member(member)
                .gameSetId(gameSetId)
                .gameId(gameId)
                .active(true)
                .isEndGameSet(false)
                .build();
    }
}
