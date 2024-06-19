package balancetalk.module.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class BestCommentResponse {
    @Schema(description = "댓글 id", example = "1345")
    private Long id;

    @Schema(description = "해당 댓글에 맞는 게시글 id", example = "1")
    private Long postId;

    @Schema(description = "해당 댓글에 맞는 게시글 제목", example = "주먹밥 뭐 좋아함?")
    private String postTitle;

    @Schema(description = "답글 작성자", example = "훈이")
    private String nickname;

    @Schema(description = "댓글 내용", example = "나 HTML인데 프로그래밍 언어 맞다")
    private String content;

    @Schema(description = "해당 답글에 맞는 선택지", example = "B")
    private balancetalk.module.vote.dto.Option Option;

    @Schema(description = "댓글 좋아요 수", example = "24")
    private int likesCount;

    @Schema(description = "현재 사용자의 좋아요 여부", example = "true")
    private Boolean myLike;

    @Schema(description = "답글 생성 날짜")
    private LocalDateTime createdAt;

    @Schema(description = "답글 수정 날짜")
    private LocalDateTime lastModifiedAt;

    @Schema(description = "베스트 댓글 여부", example = "true")
    private Boolean isBest;
}
