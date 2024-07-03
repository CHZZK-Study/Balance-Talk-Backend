package balancetalk.comment.presentation;

import balancetalk.comment.application.CommentService;
import balancetalk.comment.dto.CommentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/talks/{talkPickId}/comments")
@RequiredArgsConstructor
@Tag(name = "comment", description = "댓글 API")
public class CommentController {

    private static final String SUCCESS_RESPONSE_MESSAGE = "OK"; // TODO : 상수 클래스로 분리

    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "댓글 작성", description = "talkPick-id에 해당하는 게시글에 댓글을 작성한다.")
    public String createComment(@PathVariable Long talkPickId, @Valid @RequestBody CommentDto.CreateCommentRequest createCommentRequest) {
        commentService.createComment(createCommentRequest, talkPickId);
        return SUCCESS_RESPONSE_MESSAGE;
    }

    @GetMapping
    @Operation(summary = "최신 댓글 목록 조회", description = "talkPick-id에 해당하는 게시글에 있는 모든 댓글 및 답글을 최신순으로 정렬해 조회한다.")
    public Page<CommentDto.CommentResponse> findAllCommentsByPostIdSortedByCreatedAt(@PathVariable Long talkPickId, Pageable pageable,
                                                                    @RequestHeader(value = "Authorization", required = false) String token) {
        Pageable sortedByCreatedAtDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by("createdAt").descending());
        return commentService.findAllComments(talkPickId, token, sortedByCreatedAtDesc);
    }

    @GetMapping("/best")
    @Operation(summary = "베스트 댓글 목록 조회", description = "talkPick-id에 해당하는 게시글에 있는 모든 댓글 및 답글을 베스트 및 좋아요 순으로 정렬해 조회한다.")
    public Page<CommentDto.CommentResponse> findAllBestCommentsByPostId(@PathVariable Long talkPickId, Pageable pageable,
                                                                        @RequestHeader(value = "Authorization", required = false) String token) {
        return commentService.findAllBestComments(talkPickId, pageable);
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

    @PutMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "commentId에 해당하는 댓글 내용을 수정한다.")
    public String updateComment(@PathVariable Long commentId, @PathVariable Long talkPickId, @RequestBody CommentDto.UpdateCommentRequest request) {
            commentService.updateComment(commentId, talkPickId, request.getContent());
        return SUCCESS_RESPONSE_MESSAGE;
    }


    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "commentId에 해당하는 댓글을 삭제한다.")
    public String deleteComment(@PathVariable Long commentId, @PathVariable Long talkPickId) {
        commentService.deleteComment(commentId, talkPickId);
        return SUCCESS_RESPONSE_MESSAGE;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{commentId}/replies")
    @Operation(summary = "답글 작성", description = "commentId에 해당하는 댓글에 답글을 작성한다.")
    public String createReply(@PathVariable Long commentId, @Valid @RequestBody CommentDto.CreateCommentRequest createCommentRequest) {
        return SUCCESS_RESPONSE_MESSAGE;
    }

    /*
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{commentId}/replies")
    @Operation(summary = "답글 목록 조회", description = "post-id 하위의 comment-id에 해당하는 댓글에 있는 모든 답글을 조회한다.")
    public List<ReplyResponse> findAllReplies(@PathVariable Long postId, @PathVariable Long commentId,
                                              @RequestHeader(value = "Authorization", required = false) String token) {
        return commentService.findAllReplies(postId, commentId, token);
    }
    */
    /*

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{commentId}/report")
    @Operation(summary = "댓글 신고", description = "comment-id에 해당하는 댓글을 신고 처리한다.")
    public String reportComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestBody ReportRequest reportRequest) {
        commentService.reportComment(postId, commentId, reportRequest);
        return "신고가 성공적으로 접수되었습니다.";
    }

     */
}
