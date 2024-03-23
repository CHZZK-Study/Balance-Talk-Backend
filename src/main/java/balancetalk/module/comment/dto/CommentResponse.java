package balancetalk.module.comment.dto;

import balancetalk.module.comment.domain.Comment;
import balancetalk.module.file.domain.File;
import balancetalk.module.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Optional;

@Data
@AllArgsConstructor
@Builder
public class CommentResponse {

    @Schema(description = "댓글 id", example = "1345")
    private Long id;

    @Schema(description = "댓글 내용", example = "댓글 내용...")
    private String content;

    @Schema(description = "댓글 작성자", example = "닉네임")
    private String memberName;

    @Schema(description = "해당 댓글에 맞는 게시글 id", example = "1")
    private Long postId;
  
    @Schema(description = "해당 댓글에 맞는 선택지 id", example = "23")
    private Long selectedOptionId;

    @Schema(description = "부모 댓글 id", example = "2")
    private Long parentCommentId;

    @Schema(description = "댓글 추천 수", example = "24")
    private int likesCount;

    @Schema(description = "추천 여부", example = "true")
    private boolean myLike;

    @Schema(description = "댓글 생성 날짜")
    private LocalDateTime createdAt;

    @Schema(description = "댓글 수정 날짜")
    private LocalDateTime lastModifiedAt;

    @Schema(description = "댓글 작성자 프로필 사진 경로", example = "https://balance-talk-static-files4df23447-2355-45h2-8783-7f6gd2ceb848_프로필.jpg")
    private String profileImageUrl;

    @Schema(description = "게시글 제목", example = "게시글 제목...")
    private String postTitle;

    public static CommentResponse fromEntity(Comment comment, Long balanceOptionId, boolean myLike) {
        return CommentResponse.builder()
                .id(comment.getId())
                .postTitle(comment.getPost().getTitle())
                .content(comment.getContent())
                .memberName(comment.getMember().getNickname())
                .postId(comment.getPost().getId())
                .selectedOptionId(balanceOptionId)
                .parentCommentId(getParentCommentId(comment))
                .likesCount(comment.getLikes().size())
                .myLike(myLike)
                .createdAt(comment.getCreatedAt())
                .lastModifiedAt(comment.getLastModifiedAt())
                .profileImageUrl(getProfileImageUrl(comment.getMember()))
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
