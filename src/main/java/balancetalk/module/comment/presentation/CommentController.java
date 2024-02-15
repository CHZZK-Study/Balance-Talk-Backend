package balancetalk.module.comment.presentation;

import balancetalk.module.comment.application.CommentService;
import balancetalk.module.comment.domain.Comment;
import balancetalk.module.comment.dto.CommentCreateRequest;
import balancetalk.module.comment.dto.CommentResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public String createComment(@PathVariable Long postId, @RequestBody CommentCreateRequest request) {
        commentService.createComment(request, postId);
        return "댓글이 정상적으로 작성되었습니다.";
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    public List<CommentResponse> findAllCommentsByPostId(@PathVariable Long postId) {
        return commentService.findAll(postId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{commentId}")
    public String updateComment(@PathVariable Long commentId, @RequestBody CommentCreateRequest request) {
        commentService.updateComment(commentId, request.getContent());
        return "댓글이 정상적으로 수정되었습니다.";
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{commentId}")
    public String deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return "댓글이 정상적으로 삭제되었습니다.";
    }
}
