package balancetalk.module.comment.dto;

import balancetalk.module.ViewStatus;
import balancetalk.module.comment.domain.Comment;
import balancetalk.module.member.domain.Member;
import balancetalk.module.post.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentCreateRequest {

    @Schema(description = "댓글 내용", example = "댓글 내용...")
    private String content;

    @Schema(description = "회원 id", example = "1")
    private Long memberId;
  
    @Schema(description = "선택지 id", example = "23")
    private Long selectedOptionId;

    public Comment toEntity(Member member, Post post) {
        return Comment.builder()
                .content(content)
                .member(member)
                .post(post)
                .viewStatus(ViewStatus.NORMAL)
                .build();
    }
}
