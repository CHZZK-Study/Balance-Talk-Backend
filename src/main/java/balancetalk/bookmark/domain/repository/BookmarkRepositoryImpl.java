package balancetalk.bookmark.domain.repository;

import balancetalk.bookmark.domain.BookmarkType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static balancetalk.bookmark.domain.QBookmark.*;

@RequiredArgsConstructor
public class BookmarkRepositoryImpl implements BookmarkRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public long countBookmarksByResourceIdAndType(long resourceId, BookmarkType type) {
        return queryFactory
                .select(bookmark)
                .from(bookmark)
                .where(bookmark.resourceId.eq(resourceId), bookmark.bookmarkType.eq(type))
                .stream().count();
    }
}
