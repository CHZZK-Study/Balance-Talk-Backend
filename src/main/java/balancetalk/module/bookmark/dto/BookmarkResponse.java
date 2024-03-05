package balancetalk.module.bookmark.dto;

import balancetalk.module.bookmark.domain.Bookmark;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookmarkResponse {

    @Schema(description = "북마크 id", example = "5")
    private Long id;

    @Schema(description = "게시글 제목", example = "게시글 제목")
    private String postTitle;

    @Schema(description = "투료 종료 기한", example = "2024-03-16 08:27:17.391706\t")
    private LocalDateTime deadline;

    public static BookmarkResponse fromEntity(Bookmark bookmark) {
        return BookmarkResponse.builder()
                .id(bookmark.getId())
                .postTitle(bookmark.getPost().getTitle())
                .deadline(bookmark.getPost().getDeadline())
                .build();
    }
}
