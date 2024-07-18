package balancetalk.like.presentation;

import balancetalk.global.utils.AuthPrincipal;
import balancetalk.like.application.CommentLikeService;
import balancetalk.member.dto.ApiMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes/talks/{talkPickId}/comments/{commentId}")
@RequiredArgsConstructor
@Tag(name = "like", description = "좋아요 API")
public class LikeController {

    private final CommentLikeService commentLikeService;

    @PostMapping
    @Operation(summary = "댓글 좋아요", description = "commentId에 해당하는 댓글에 좋아요를 활성화합니다.")
    public void likeComment(@PathVariable Long commentId, @PathVariable Long talkPickId, @AuthPrincipal ApiMember apiMember) {
        commentLikeService.likeComment(commentId, talkPickId, apiMember);
    }

    @DeleteMapping
    @Operation(summary = "댓글 좋아요 취소", description = "commentId에 해당하는 댓글의 좋아요를 취소합니다.")
    public void unlikeComment(@PathVariable Long commentId, @PathVariable Long talkPickId, @AuthPrincipal ApiMember apiMember) {
        commentLikeService.unLikeComment(commentId, talkPickId, apiMember);
    }
}
