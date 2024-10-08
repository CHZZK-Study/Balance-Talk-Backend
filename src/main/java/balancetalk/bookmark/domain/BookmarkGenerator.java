package balancetalk.bookmark.domain;

import balancetalk.member.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class BookmarkGenerator {

    public TalkPickBookmark generate(final long resourceId, final BookmarkType bookmarkType, final Member member) {
        return TalkPickBookmark.builder()
                .member(member)
                .resourceId(resourceId)
                .bookmarkType(bookmarkType)
                .active(true)
                .build();
    }

    public GameBookmark generate(final long resourceId, final long gameId, final BookmarkType bookmarkType, final Member member) {
        return GameBookmark.builder()
                .member(member)
                .resourceId(resourceId)
                .gameId(gameId)
                .bookmarkType(bookmarkType)
                .active(true)
                .isEndGameSet(false)
                .build();
    }
}
