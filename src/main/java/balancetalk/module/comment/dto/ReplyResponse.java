package balancetalk.module.comment.dto;

import balancetalk.module.comment.domain.Comment;
import balancetalk.module.file.domain.File;
import balancetalk.module.member.domain.Member;
import balancetalk.module.vote.domain.Option;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@AllArgsConstructor
@Builder
public class ReplyResponse {
    @Schema(description = "답글 id", example = "1345")
    private Long id;

    @Schema(description = "부모 댓글 id", example = "2")
    private Long parentCommentId; // TODO : 댓글 정렬을 백엔드에서 한다면, 필요 없을 수도 있음.
    // TODO : ReplyResponse를 CommentResponse에 통합하고, isBest는 BestCommentResponse에만 존재.

    @Schema(description = "해당 답글에 맞는 게시글 id", example = "1")
    private Long postId;

    @Schema(description = "답글 작성자", example = "닉네임")
    private String nickname;

    @Schema(description = "답글 내용", example = "댓글 내용...")
    private String content;

    @Schema(description = "해당 답글에 맞는 선택지", example = "B")
    private Option Option;

    @Schema(description = "답글 좋아요 수", example = "24")
    private int likesCount;

    @Schema(description = "현재 사용자의 좋아요 여부", example = "true")
    private Boolean myLike;

    @Schema(description = "답글 생성 날짜")
    private LocalDateTime createdAt;

    @Schema(description = "답글 수정 날짜")
    private LocalDateTime lastModifiedAt;

    @Schema(description = "베스트 댓글 여부", example = "true")
    private Boolean isBest;

    public static ReplyResponse fromEntity(Comment comment, Long balanceOptionId, boolean myLike) {
        return ReplyResponse.builder()
                .id(comment.getId())
                .parentCommentId(getParentCommentId(comment))
                .content(comment.getContent())
                .nickname(comment.getMember().getNickname())
                .postId(comment.getPost().getId())
                .Option(balancetalk.module.vote.domain.Option.B) //TODO : 옵션 관리 방법에 따라 추후 재구현
                .likesCount(comment.getLikes().size())
                .myLike(myLike)
                .createdAt(comment.getCreatedAt())
                .lastModifiedAt(comment.getLastModifiedAt())
                // TODO : 구현 필요 .isBest(comment.isBest())
                .build();
    }


    private static String getProfileImageUrl(Member member) {
        return Optional.ofNullable(member.getProfilePhoto())
                .map(File::getUrl)
                .orElse(null);
    }

    private static Long getParentCommentId(Comment comment) {
        return Optional.ofNullable(comment.getParent())
                .map(Comment::getId)
                .orElse(null);
    }
}
