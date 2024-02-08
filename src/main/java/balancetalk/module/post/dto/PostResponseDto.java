package balancetalk.module.post.dto;

import balancetalk.module.ViewStatus;
import balancetalk.module.post.domain.BalanceOption;
import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostCategory;
import balancetalk.module.post.domain.PostTag;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private String title;
    private LocalDateTime deadline;
    private Long views;
    private PostCategory category;
    private List<BalanceOptionDto> balanceOptions;
    private List<PostTagDto> postTags;


    // todo: likeCount, createdBy, ProfilePhoto 추가
    public static PostResponseDto fromEntity(Post post) {
        List<BalanceOptionDto> balanceOptionDtos = post.getOptions().stream()
                .map(BalanceOptionDto::fromEntity)
                .collect(Collectors.toList());

        List<PostTagDto> postTagDtos = post.getPostTags().stream()
                .map(PostTagDto::fromEntity)
                .collect(Collectors.toList());

        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .deadline(post.getDeadline())
                .views(post.getViews())
                .category(post.getCategory())
                .balanceOptions(balanceOptionDtos)
                .postTags(postTagDtos)
                .build();
    }
}
