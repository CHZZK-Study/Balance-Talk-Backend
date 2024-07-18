package balancetalk.comment.presentation;

import balancetalk.comment.application.CommentService;
import balancetalk.comment.dto.CommentDto.CommentResponse;
import balancetalk.comment.dto.CommentDto.CreateCommentRequest;
import balancetalk.comment.dto.CommentDto.UpdateCommentRequest;
import balancetalk.global.utils.AuthPrincipal;
import balancetalk.member.dto.ApiMember;
import balancetalk.member.dto.GuestOrApiMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/talks/{talkPickId}/comments")
@RequiredArgsConstructor
@Tag(name = "comment", description = "댓글 API")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "댓글 작성", description = "talkPick-id에 해당하는 게시글에 댓글을 작성한다.")
    public void createComment(@PathVariable Long talkPickId, @Valid @RequestBody CreateCommentRequest createCommentRequest,
                              @AuthPrincipal ApiMember apiMember) {
        commentService.createComment(createCommentRequest, talkPickId, apiMember);
    }

    @PostMapping("/{commentId}/replies")
    @Operation(summary = "답글 작성", description = "commentId에 해당하는 댓글에 답글을 작성한다.")
    public void createCommentReply(@PathVariable Long talkPickId, @PathVariable Long commentId,
                                   @Valid @RequestBody CreateCommentRequest createCommentRequest,
                                   @AuthPrincipal ApiMember apiMember) {
        commentService.createCommentReply(createCommentRequest, talkPickId, commentId, apiMember);
    }

    @GetMapping
    @Operation(summary = "최신 댓글 목록 조회", description = "talkPick-id에 해당하는 게시글에 있는 모든 댓글 및 답글을 최신순으로 정렬해 조회한다.")
    public Page<CommentResponse> findAllCommentsByPostIdSortedByCreatedAt(@PathVariable Long talkPickId, Pageable pageable,
                                                                          @AuthPrincipal GuestOrApiMember guestOrApiMember) {
        Pageable sortedByCreatedAtDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by("createdAt").descending());
        return commentService.findAllComments(talkPickId, sortedByCreatedAtDesc, guestOrApiMember);
    }

    @GetMapping("/best")
    @Operation(summary = "베스트 댓글 목록 조회", description = "talkPick-id에 해당하는 게시글에 있는 모든 댓글 및 답글을 베스트 및 좋아요 순으로 정렬해 조회한다.")
    public Page<CommentResponse> findAllBestCommentsByPostId(@PathVariable Long talkPickId, Pageable pageable,
                                                             @AuthPrincipal GuestOrApiMember guestOrApiMember) {
        return commentService.findAllBestComments(talkPickId, pageable, guestOrApiMember);
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "commentId에 해당하는 댓글 내용을 수정한다.")
    public void updateComment(@PathVariable Long commentId, @PathVariable Long talkPickId,
                              @RequestBody UpdateCommentRequest request, @AuthPrincipal ApiMember apiMember) {
        commentService.updateComment(commentId, talkPickId, request.getContent(), apiMember);
    }


    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "commentId에 해당하는 댓글을 삭제한다.")
    public void deleteComment(@PathVariable Long commentId, @PathVariable Long talkPickId, @AuthPrincipal ApiMember apiMember) {
        commentService.deleteComment(commentId, talkPickId, apiMember);
    }
}
