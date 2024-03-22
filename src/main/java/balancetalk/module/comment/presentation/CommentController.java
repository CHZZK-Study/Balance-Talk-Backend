package balancetalk.module.comment.presentation;

import balancetalk.module.comment.application.CommentService;
import balancetalk.module.comment.dto.CommentRequest;
import balancetalk.module.comment.dto.CommentResponse;
import balancetalk.module.comment.dto.ReplyCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
@Tag(name = "comment", description = "댓글 API")
public class CommentController {

    private final CommentService commentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "댓글 작성", description = "post-id에 해당하는 게시글에 댓글을 작성한다.")
    public String createComment(@PathVariable Long postId, @Valid @RequestBody CommentRequest request) {
        commentService.createComment(request, postId);
        return "댓글이 정상적으로 작성되었습니다.";
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @Operation(summary = "댓글 목록 조회", description = "post-id에 해당하는 게시글에 있는 모든 댓글을 조회한다.")
    public Page<CommentResponse> findAllCommentsByPostId(@PathVariable Long postId, Pageable pageable,
                                                         @RequestHeader(value = "Authorization", required = false) String token) {
        return commentService.findAllComments(postId, token, pageable);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/best")
    @Operation(summary = "인기 댓글 조회", description = "추천 수가 가장 많은 댓글을 각 선택지별로 3개씩 조회한다.")
    public List<CommentResponse> findBestComments(@PathVariable Long postId,
                                                  @RequestHeader(value = "Authorization", required = false) String token) {
        return commentService.findBestComments(postId, token);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "comment-id에 해당하는 댓글 내용을 수정한다.")
    public String updateComment(@PathVariable Long commentId, @PathVariable Long postId, @RequestBody CommentRequest request) {
        commentService.updateComment(commentId, postId, request.getContent());
        return "댓글이 정상적으로 수정되었습니다.";
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "comment-id에 해당하는 댓글을 삭제한다.")
    public String deleteComment(@PathVariable Long commentId, @PathVariable Long postId) {
        commentService.deleteComment(commentId, postId);
        return "댓글이 정상적으로 삭제되었습니다.";
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{commentId}/replies")
    @Operation(summary = "답글 작성", description = "comment-id에 해당하는 댓글에 답글을 작성한다.")
    public String createComment(@PathVariable Long postId, @PathVariable Long commentId, @Valid @RequestBody ReplyCreateRequest request) {
        commentService.createReply(postId, commentId, request);
        return "답글이 정상적으로 작성되었습니다.";
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{commentId}/likes")
    @Operation(summary = "댓글 추천", description = "comment-id에 해당하는 댓글에 추천을 누른다.")
    public String likeComment(@PathVariable Long postId, @PathVariable Long commentId) {
        commentService.likeComment(postId, commentId);
        return "요청이 정상적으로 처리되었습니다.";
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{commentId}/likes")
    @Operation(summary = "댓글 추천 취소", description = "comment-id에 해당하는 댓글에 누른 추천을 취소한다.")
    public void cancelLikeComment(@PathVariable Long commentId) {
        commentService.cancelLikeComment(commentId);
    }
}
