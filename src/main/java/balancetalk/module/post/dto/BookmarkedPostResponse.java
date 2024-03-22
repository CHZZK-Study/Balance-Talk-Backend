package balancetalk.module.post.dto;

import balancetalk.module.bookmark.domain.Bookmark;
import balancetalk.module.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookmarkedPostResponse {
    private String postTitle;
    private LocalDateTime bookmarkedAt;

    public static BookmarkedPostResponse fromEntity(Bookmark bookmark) {
        return BookmarkedPostResponse.builder()
                .postTitle(bookmark.getPost().getTitle())
                .bookmarkedAt(bookmark.getCreatedAt())
                .build();
    }
}
