package balancetalk.module.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String content;
    private String memberName;
    private Long postId;
    private int likeCount;
}
