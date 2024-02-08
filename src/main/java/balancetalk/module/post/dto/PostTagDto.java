package balancetalk.module.post.dto;

import balancetalk.module.post.domain.PostTag;
import balancetalk.module.post.domain.Tag;
import lombok.*;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostTagDto {

    private String tagName;
    public PostTag toEntity() {
        return PostTag.builder()
                .tag(new Tag(tagName))
                .build();
    }

    public static PostTagDto fromEntity(PostTag postTag) {
        return PostTagDto.builder()
                .tagName(postTag.getTag().getName())
                .build();
    }
}
