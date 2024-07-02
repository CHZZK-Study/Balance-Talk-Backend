package balancetalk.like.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/talks")
@RequiredArgsConstructor
@Tag(name = "like", description = "좋아요 API")
public class LikeController {

    private static final String SUCCESS_RESPONSE_MESSAGE = "OK";

    @PostMapping("/{talkPickId}/{commentId}/likes")
    @Operation(summary = "댓글 좋아요", description = "commentId에 해당하는 댓글에 좋아요를 활성화합니다.")
    public String likeComment(@PathVariable Long commentId) {
        //commentService.likeComment(postId, commentId);
        return SUCCESS_RESPONSE_MESSAGE;
    }

    @DeleteMapping("/{talkPickId}/{commentId}/likes")
    @Operation(summary = "댓글 좋아요 취소", description = "commentId에 해당하는 댓글의 좋아요를 취소합니다.")
    public String cancelLikeComment(@PathVariable Long commentId) {
        //commentService.cancelLikeComment(commentId);
        return SUCCESS_RESPONSE_MESSAGE;
    }
}
