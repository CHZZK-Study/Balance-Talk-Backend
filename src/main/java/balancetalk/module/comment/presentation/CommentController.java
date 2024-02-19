package balancetalk.module.comment.presentation;

import balancetalk.module.comment.application.CommentService;
import balancetalk.module.comment.domain.Comment;
import balancetalk.module.comment.dto.CommentCreateRequest;
import balancetalk.module.comment.dto.CommentResponse;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "comment", description = "댓글 API")
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "댓글 작성", description = "post-id에 해당하는 게시글에 댓글을 작성한다.")
    public String createComment(@PathVariable Long postId, @RequestBody CommentCreateRequest request) {
        commentService.createComment(request, postId);
        return "댓글이 정상적으로 작성되었습니다.";
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @Operation(summary = "댓글 목록 조회", description = "post-id에 해당하는 게시글에 있는 모든 댓글을 조회한다.")
    public List<CommentResponse> findAllCommentsByPostId(@PathVariable Long postId) {
        return commentService.findAll(postId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "comment-id에 해당하는 댓글 내용을 수정한다.")
    public String updateComment(@PathVariable Long commentId, @RequestBody CommentCreateRequest request) {
        commentService.updateComment(commentId, request.getContent());
        return "댓글이 정상적으로 수정되었습니다.";
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "comment-id에 해당하는 댓글을 삭제한다.")
    public String deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return "댓글이 정상적으로 삭제되었습니다.";
    }

    @PostMapping("/{commentId}/likes")
    @Operation(summary = "댓글 추천", description = "comment-id에 해당하는 댓글에 추천을 누른다.")
    public ResponseEntity<String> likeComment(@PathVariable Long postId,
                                              @PathVariable Long commentId,
                                              @RequestBody Long memberId) {
        Long likedCommentId = commentService.likeComment(postId, commentId, memberId);
        String contentLocation = String.format("/posts/%d/comments/%d/likes", postId, likedCommentId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Content-Location", contentLocation)
                .body("요청이 정상적으로 처리되었습니다.");
    }
}
