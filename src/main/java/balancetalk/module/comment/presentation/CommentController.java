package balancetalk.module.comment.presentation;

import balancetalk.module.comment.application.CommentService;
import balancetalk.module.comment.domain.Comment;
import balancetalk.module.comment.dto.CommentCreateRequest;
import balancetalk.module.comment.dto.CommentResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<?> createComment(@PathVariable Long postId, @RequestBody CommentCreateRequest request) {
        try {
            CommentResponse createdCommentResponse = commentService.createComment(request, postId);
            return new ResponseEntity<>(createdCommentResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage()); //TODO : NULL 처리?
        }
    }

    @GetMapping()
    public ResponseEntity<?> getCommentsByPostId(@PathVariable Long postId) {
        try {
            List<CommentResponse> comments = commentService.readCommentsByPostId(postId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId, @RequestBody CommentCreateRequest request) {
        try {
            Comment updatedComment = commentService.updateComment(commentId, request.getContent());
            CommentResponse response = CommentResponse.fromEntity(updatedComment);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            commentService.deleteComment(commentId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{commentId}/likes")
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
