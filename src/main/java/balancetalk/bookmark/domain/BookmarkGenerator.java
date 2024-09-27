package balancetalk.bookmark.domain;

import balancetalk.member.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class BookmarkGenerator {

    public Bookmark generate(final long resourceId, final BookmarkType bookmarkType, final Member member) {
        return Bookmark.builder()
                .member(member)
                .resourceId(resourceId)
                .bookmarkType(bookmarkType)
                .active(true)
                .build();
    }

    public Bookmark generate(final long resourceId, final long gameId, final BookmarkType bookmarkType, final Member member) {
        return Bookmark.builder()
                .member(member)
                .resourceId(resourceId)
                .gameId(gameId)
                .bookmarkType(bookmarkType)
                .active(true)
                .build();
    }
}
