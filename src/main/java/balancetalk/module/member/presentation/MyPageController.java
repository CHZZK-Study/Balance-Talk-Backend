package balancetalk.module.member.presentation;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.module.comment.application.CommentService;
import balancetalk.module.comment.dto.CommentResponse;
import balancetalk.module.post.application.PostService;
import balancetalk.module.post.dto.BookmarkedPostResponse;
import balancetalk.module.post.dto.PostResponse;
import balancetalk.module.post.dto.VotedPostResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static balancetalk.global.exception.ErrorCode.PAGE_NUMBER_ZERO;
import static balancetalk.global.exception.ErrorCode.PAGE_SIZE_ZERO;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/myPage")
@Tag(name = "myPage", description = "마이페이지 API")

public class MyPageController {

    private final PostService postService;
    private final CommentService commentService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/history/posts")
    @Operation(summary = "모든 게시글 조회", description = "해당 회원이 쓴 모든 글을 조회한다.")
    public Page<PostResponse> findAllPosts(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(required = false, value = "size", defaultValue = "10") int size) {

        validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return postService.findAllByCurrentMember(pageable);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/history/comments")
    @Operation(summary = "모든 댓글 조회", description = "해당 회원이 쓴 모든 댓글을 조회한다.")
    public Page<CommentResponse> findAllComments(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(required = false, value = "size", defaultValue = "10") int size) {

        validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return commentService.findAllByCurrentMember(pageable);
    }

    @GetMapping("/history/votedPosts")
    public Page<VotedPostResponse> findAllVotedPosts(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(required = false, value = "size", defaultValue = "10") int size) {

        validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return postService.findAllVotedByCurrentMember(pageable);
    }

    @GetMapping("/history/bookmarks")
    public Page<BookmarkedPostResponse> findAllBookmarkedPosts(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(required = false, value = "size", defaultValue = "10") int size) {

        validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return postService.findAllBookmarkedByCurrentMember(pageable);
    }

    private void validatePageNumberAndSize(int page, int size) {
        if (page <= 0) {
            throw new BalanceTalkException(PAGE_NUMBER_ZERO);
        }
        if (size <= 0) {
            throw new BalanceTalkException(PAGE_SIZE_ZERO);
        }
    }
}
