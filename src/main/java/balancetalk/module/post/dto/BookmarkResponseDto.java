package balancetalk.module.post.dto;

import balancetalk.module.post.domain.Bookmark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookmarkResponseDto {
    private Long id;
    private String postTitle;
    private LocalDateTime deadline;

    public static BookmarkResponseDto fromEntity(Bookmark bookmark) {
        return BookmarkResponseDto.builder()
                .id(bookmark.getId())
                .postTitle(bookmark.getPost().getTitle())
                .deadline(bookmark.getPost().getDeadline())
                .build();
    }
}
