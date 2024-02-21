package balancetalk.module.post.dto;

import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
public class PostResponseDto {

    @Schema(description = "게시글 id", example = "1")
    private Long id;

    @Schema(description = "게시글 제목", example = "게시글 제목")
    private String title;

    @Schema(description = "투료 종료 기한", example = "2024-03-16 08:27:17.391706\t")
    private LocalDateTime deadline;

    @Schema(description = "게시글 조회 수", example = "126")
    private Long views;

    @Schema(description = "게시글 카테고리", example = "CASUAL")
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
