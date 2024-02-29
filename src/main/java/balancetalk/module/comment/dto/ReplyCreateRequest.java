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
public class ReplyCreateRequest {

    @Schema(description = "답글 내용", example = "답글 내용...")
    private String content;

    @Schema(description = "회원 id", example = "1")
    private Long memberId;

    @Schema(description = "댓글 id", example = "1")
    private Long commentId;

    public Comment toEntity(Member member, Post post, Comment parentComment) {
        return Comment.builder()
                .content(content)
                .member(member)
                .parent(parentComment)
                .post(post)
                .viewStatus(ViewStatus.NORMAL)
                .build();
    }
}
