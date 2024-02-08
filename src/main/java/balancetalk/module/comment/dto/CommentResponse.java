package balancetalk.module.comment.dto;

import balancetalk.module.comment.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Long id;
    private String content;
    private String memberName;
    private Long postId;
    private int likeCount;

    public static CommentResponse fromEntity(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .memberName(comment.getMember().getNickname())
                .postId(comment.getPost().getId())
                // .likeCount(comment.getLikes().size()) //TODO: likeCount가 NULL이라 Response 어려움
                .build();
    }
}
