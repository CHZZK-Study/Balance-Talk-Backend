package balancetalk.like.presentation;

import balancetalk.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/talks")
@RequiredArgsConstructor
@Tag(name = "like", description = "좋아요 API")
public class LikeController {
    @PostMapping("/{talkPickId}/{commentId}/likes")
    @Operation(summary = "댓글 좋아요", description = "talkPick-id에 해당하는 댓글에 추천을 누른다.")
    public ApiResponse<String> likeComment(@PathVariable Long talkPickId, @PathVariable Long commentId) {
        //commentService.likeComment(postId, commentId);
        return ApiResponse.ok("댓글 좋아요가 정상적으로 처리되었습니다.");
    }

    @DeleteMapping("/{talkPickId}/{commentId}/likes")
    @Operation(summary = "댓글 좋아요 취소", description = "talkPick-id에 해당하는 댓글에 누른 추천을 취소한다.")
    public void cancelLikeComment(@PathVariable Long commentId) {
        //commentService.cancelLikeComment(commentId);
    }
}
