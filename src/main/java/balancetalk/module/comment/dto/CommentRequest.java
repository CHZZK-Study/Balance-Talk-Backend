package balancetalk.module.comment.dto;

import balancetalk.module.ViewStatus;
import balancetalk.module.comment.domain.Comment;
import balancetalk.module.member.domain.Member;
import balancetalk.module.post.domain.Post;
import balancetalk.module.vote.dto.Option;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentRequest {

    @Schema(description = "댓글 내용", example = "댓글 내용...")
    private String content;
  
    @Schema(description = "선택지", example = "A")
    private Option option;

    public Comment toEntity(Member member, Post post) {
        return Comment.builder()
                .content(content)
                .member(member)
                .post(post)
                .viewStatus(ViewStatus.NORMAL)
                .build();
    }
}
