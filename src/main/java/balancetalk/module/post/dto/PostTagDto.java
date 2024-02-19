package balancetalk.module.post.dto;

import balancetalk.module.post.domain.PostTag;
import balancetalk.module.post.domain.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostTagDto {

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
