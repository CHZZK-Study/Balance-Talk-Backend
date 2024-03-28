package balancetalk.module.post.dto;

import balancetalk.module.post.domain.PostTag;
import balancetalk.module.post.domain.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class PostTagDto {

    @Schema(description = "게시글 태그", example = "태그1")
    private String tagName;

    public PostTag toEntity() {
        return PostTag.builder()
                .tag(Tag.builder().name(tagName).build())
                .build();
    }

    public static PostTagDto fromEntity(PostTag postTag) {
        return PostTagDto.builder()
                .tagName(postTag.getTag().getName())
                .build();
    }
}
