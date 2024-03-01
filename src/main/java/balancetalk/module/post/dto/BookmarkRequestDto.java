package balancetalk.module.post.dto;

import balancetalk.module.member.domain.Member;
import balancetalk.module.post.domain.Bookmark;
import balancetalk.module.post.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkRequestDto {
    @Schema(description = "게시글 id", example = "1")
    private Long postId;

    public Bookmark toEntity(Member member, Post post) {
        return Bookmark.builder()
                .member(member)
                .post(post)
                .build();
    }
}
