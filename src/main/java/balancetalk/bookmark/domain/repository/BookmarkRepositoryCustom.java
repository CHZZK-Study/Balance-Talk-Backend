package balancetalk.bookmark.domain.repository;

import balancetalk.bookmark.domain.BookmarkType;

public interface BookmarkRepositoryCustom {

    Long countBookmarksByResourceIdAndType(long resourceId, BookmarkType type);
}
