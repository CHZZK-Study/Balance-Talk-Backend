package balancetalk.like.presentation;

import balancetalk.like.application.CommentLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/talks/likes/{talkPickId}/comments/{commentId}")
@RequiredArgsConstructor
@Tag(name = "like", description = "좋아요 API")
public class LikeController {

    private final CommentLikeService commentLikeService;

    @PostMapping
    @Operation(summary = "댓글 좋아요", description = "commentId에 해당하는 댓글에 좋아요를 활성화합니다.")
    public void likeComment(@PathVariable Long commentId, @PathVariable Long talkPickId) {
        commentLikeService.likeComment(commentId, talkPickId);
    }

    @DeleteMapping
    @Operation(summary = "댓글 좋아요 취소", description = "commentId에 해당하는 댓글의 좋아요를 취소합니다.")
    public void unlikeComment(@PathVariable Long commentId, @PathVariable Long talkPickId) {
        commentLikeService.unLikeComment(commentId, talkPickId);
    }
}
