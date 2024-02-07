package balancetalk.module.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentUpdateRequest {
    private Long commentId;
    private String content;
}