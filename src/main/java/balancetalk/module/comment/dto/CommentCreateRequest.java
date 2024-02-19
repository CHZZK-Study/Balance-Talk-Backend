package balancetalk.module.comment.dto;

import balancetalk.module.ViewStatus;
import balancetalk.module.comment.domain.Comment;
import balancetalk.module.member.domain.Member;
import balancetalk.module.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentCreateRequest {
    private String content;
    private Long memberId;
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
