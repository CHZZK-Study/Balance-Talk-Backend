package balancetalk.module.post.dto;

import balancetalk.module.ViewStatus;
import balancetalk.module.post.domain.BalanceOption;
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
public class PostResponseDto {
    private Long id;
    private String title;

    private LocalDateTime deadline;
    private Long views;
    private ViewStatus viewStatus;
    private PostCategory category;
    private List<BalanceOption> balanceOptions;
    private List<PostTag> postTags;

    public static PostResponseDto fromEntity(Post post) {
        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getDeadline(),
                post.getViews(),
                post.getViewStatus(),
                post.getCategory(),
                post.getOptions(),
                post.getPostTags()
        );
    }
}
