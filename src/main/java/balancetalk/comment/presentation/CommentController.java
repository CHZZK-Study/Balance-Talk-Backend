package balancetalk.comment.presentation;

import balancetalk.comment.application.CommentService;
import balancetalk.comment.domain.Comment;
import balancetalk.comment.dto.CommentDto;
import balancetalk.global.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/talks/{talkPickId}/comments")
@RequiredArgsConstructor
@Tag(name = "comment", description = "댓글 API")
public class CommentController {

    private final CommentService commentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "댓글 작성", description = "post-id에 해당하는 게시글에 댓글을 작성한다.")
    public ApiResponse<String> createComment(@PathVariable Long talkPickId, @Valid @RequestBody CommentDto.Request request) {
        commentService.createComment(request, talkPickId);
        return ApiResponse.ok("댓글이 정상적으로 작성되었습니다.");
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @Operation(summary = "최신 댓글 목록 조회", description = "post-id에 해당하는 게시글에 있는 모든 댓글을 최신순으로 정렬해 조회한다.")
    public ApiResponse<Page<CommentDto.Response>> findAllCommentsByPostId(@PathVariable Long talkPickId, Pageable pageable,
                                                             @RequestHeader(value = "Authorization", required = false) String token) {
        return ApiResponse.ok(commentService.findAllComments(talkPickId, token, pageable));
    }

    /*
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/best")
    @Operation(summary = "인기 댓글 조회", description = "추천 수가 가장 많은 댓글을 각 선택지별로 3개씩 조회한다.")
    public List<BestCommentResponse> findBestComments(@PathVariable Long postId,
                                                      @RequestHeader(value = "Authorization", required = false) String token) {
        return null;
    }

     */

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "comment-id에 해당하는 댓글 내용을 수정한다.")
    public ApiResponse<String> updateComment(@PathVariable Long commentId, @PathVariable Long talkPickId, @RequestBody CommentDto.Request request) {
        commentService.updateComment(commentId, talkPickId, request.getContent());
        return ApiResponse.ok("댓글이 정상적으로 수정되었습니다.");
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "comment-id에 해당하는 댓글을 삭제한다.")
    public ApiResponse<String> deleteComment(@PathVariable Long commentId, @PathVariable Long talkPickId) {
        commentService.deleteComment(commentId, talkPickId);
        return ApiResponse.ok("댓글이 정상적으로 삭제되었습니다.");
    }

    /*
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{commentId}/replies")
    @Operation(summary = "답글 작성", description = "comment-id에 해당하는 댓글에 답글을 작성한다.")
    public String createComment(@PathVariable Long postId, @PathVariable Long commentId, @Valid @RequestBody ReplyCreateRequest request) {
        commentService.createReply(postId, commentId, request);
        return "답글이 정상적으로 작성되었습니다.";
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{commentId}/replies")
    @Operation(summary = "답글 목록 조회", description = "post-id 하위의 comment-id에 해당하는 댓글에 있는 모든 답글을 조회한다.")
    public List<ReplyResponse> findAllReplies(@PathVariable Long postId, @PathVariable Long commentId,
                                              @RequestHeader(value = "Authorization", required = false) String token) {
        return commentService.findAllReplies(postId, commentId, token);
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

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{commentId}/report")
    @Operation(summary = "댓글 신고", description = "comment-id에 해당하는 댓글을 신고 처리한다.")
    public String reportComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestBody ReportRequest reportRequest) {
        commentService.reportComment(postId, commentId, reportRequest);
        return "신고가 성공적으로 접수되었습니다.";
    }

     */
}