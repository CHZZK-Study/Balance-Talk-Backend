package balancetalk.module.post.dto;

import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
public class PostResponse {

    @Schema(description = "게시글 id", example = "1")
    private Long id;

    @Schema(description = "게시글 제목", example = "게시글 제목")
    private String title;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    @Schema(description = "투료 종료 기한", example = "2024-12-25T15:30:00")
    private LocalDateTime deadline;

    @Schema(description = "게시글 조회수", example = "126")
    private long views;

    @Schema(description = "게시글 추천수", example = "15")
    private long likesCount;

    @Schema(description = "추천 여부", example = "true")
    private boolean myLike;

    @Schema(description = "북마크 여부", example = "false")
    private boolean myBookmark;

    @Schema(description = "게시글 카테고리", example = "CASUAL")
    private PostCategory category;

    private List<BalanceOptionDto> balanceOptions;

    private List<PostTagDto> postTags;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    @Schema(description = "게시글 작성일", example = "2023-12-25T15:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "게시글 작성자", example = "작성자 닉네임")
    private String createdBy;

    // todo: ProfilePhoto 추가
    public static PostResponse fromEntity(Post post, boolean myLike, boolean myBookmark) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .deadline(post.getDeadline())
                .views(post.getViews())
                .likesCount(post.likesCount())
                .myLike(myLike)
                .myBookmark(myBookmark)
                .category(post.getCategory())
                .balanceOptions(getBalanceOptions(post))
                .postTags(getPostTags(post))
                .createdAt(post.getCreatedAt())
                .createdBy(post.getMember().getNickname())
                .build();
    }

    private static List<PostTagDto> getPostTags(Post post) {
        return post.getPostTags().stream()
                .map(PostTagDto::fromEntity)
                .collect(Collectors.toList());
    }

    private static List<BalanceOptionDto> getBalanceOptions(Post post) {
        return post.getOptions().stream()
                .map(BalanceOptionDto::fromEntity)
                .collect(Collectors.toList());
    }
}
