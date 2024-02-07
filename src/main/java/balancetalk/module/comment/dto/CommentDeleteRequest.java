package balancetalk.module.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentDeleteRequest {
    private Long commentId;
}