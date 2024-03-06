package balancetalk.module.post.dto;

import balancetalk.module.member.domain.Member;
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
public class PostResponse {

    @Schema(description = "게시글 id", example = "1")
    private Long id;

    @Schema(description = "게시글 제목", example = "게시글 제목")
    private String title;

    @Schema(description = "투료 종료 기한", example = "2024-03-16 08:27:17.391706\t")
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

    @Schema(description = "게시글 작성일", example = "2022-02-12")
    private LocalDateTime createdAt;

    @Schema(description = "게시글 작성자", example = "작성자 닉네임")
    private String createdBy;

    // todo: ProfilePhoto 추가
    public static PostResponse fromEntity(Post post, Member member) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .deadline(post.getDeadline())
                .views(post.getViews())
                .likesCount(post.likesCount())
                .myLike(member.hasLiked(post))
                .myBookmark(member.hasBookmarked(post))
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
