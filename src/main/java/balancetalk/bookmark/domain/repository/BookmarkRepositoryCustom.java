package balancetalk.bookmark.domain.repository;

import balancetalk.bookmark.domain.BookmarkType;

public interface BookmarkRepositoryCustom {

    long countBookmarksByResourceIdAndType(long resourceId, BookmarkType type);
}
