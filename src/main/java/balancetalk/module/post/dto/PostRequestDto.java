package balancetalk.module.post.dto;

import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostCategory;
import balancetalk.module.post.domain.PostTag;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostRequestDto {

    private String title;
    private LocalDateTime deadline;
    private Long views;
    private PostCategory Category;
    private List<BalanceOptionDto> balanceOptions;
    private List<PostTag> tags;

    public static PostRequestDto toEntity(Post post) {
        return PostRequestDto.builder()
                .title(post.getTitle())
                .deadline(post.getDeadline())
                .views(post.getViews())
                .Category(post.getCategory())
                .balanceOptions(BalanceOptionDto.fromEntities(post.getOptions()))
                .tags(post.getPostTags())
                .build();
    }
}
