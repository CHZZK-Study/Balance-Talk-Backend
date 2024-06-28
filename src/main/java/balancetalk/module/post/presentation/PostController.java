package balancetalk.module.post.presentation;

import balancetalk.module.post.application.PostService;
import balancetalk.module.post.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("posts")
@Tag(name = "post", description = "게시글 API")
public class PostController {

    private final PostService postService;

//    @ResponseStatus(HttpStatus.CREATED)
//    @PostMapping
//    @Operation(summary = "게시글 생성", description = "로그인 상태인 회원이 게시글을 작성한다.")
//    public PostResponse createPost(@Valid @RequestBody final PostRequest postRequestDto) {
//        return postService.save(postRequestDto);
//    }

//    @ResponseStatus(HttpStatus.OK)
//    @GetMapping
//    @Operation(summary = "모든 게시글 조회", description = "해당 회원이 쓴 모든 글을 조회한다.")
//    public Page<PostResponse> findAllPosts(@RequestHeader(value = "Authorization", required = false) String token,
//                                           @Parameter(name = "closed",
//                                                   description = "마감된 게시글 포함 여부",
//                                                   example = "true",
//                                                   required = true)
//                                           @RequestParam Boolean closed,
//                                           Pageable pageable) {
//        return postService.findAll(token, closed, pageable);
//    }
//
//    @ResponseStatus(HttpStatus.OK)
//    @GetMapping("/{postId}")
//    @Operation(summary = "게시글 조회", description = "post-id에 해당하는 게시글을 조회한다.")
//    public PostResponse findPost(@PathVariable("postId") Long postId,
//                                 @RequestHeader(value = "Authorization", required = false) String token) {
//        return postService.findById(postId, token);
//    }


//
//    @ResponseStatus(HttpStatus.OK)
//    @GetMapping("/best")
//    @Operation(summary = "인기 게시글 조회", description = "월별 추천 수가 가장 많은 게시글 5개를 조회한다.")
//    public List<PostResponse> findBestPosts(@RequestHeader(value = "Authorization", required = false) String token) {
//        return postService.findBestPosts(token);
//    }
//
//    @ResponseStatus(HttpStatus.OK)
//    @GetMapping("/title")
//    @Operation(summary = "게시글 제목 검색 기능", description = "키워드에 맞는 모든 게시글을 조회한다.")
//    public List<PostResponse> findPostsByTitle(@RequestHeader(value = "Authorization", required = false) String token,
//                                               @RequestParam String keyword) {
//        return postService.findPostsByTitle(token, keyword);
//    }
//
//    @ResponseStatus(HttpStatus.OK)
//    @GetMapping("/tag")
//    @Operation(summary = "게시글 태그 검색 기능", description = "태그에 맞는 모든 게시글을 조회한다.")
//    public List<PostResponse> findPostsByTag(@RequestHeader(value = "Authorization", required = false) String token,
//                                             @RequestParam String tagName) {
//        return postService.findPostsByTag(token, tagName);
//    }
//
//    @ResponseStatus(HttpStatus.CREATED)
//    @DeleteMapping("/{postId}")
//    @Operation(summary = "게시글 삭제", description = "post-id에 해당하는 게시글을 삭제한다.")
//    public String deletePost(@PathVariable("postId") Long postId) {
//        postService.deleteById(postId);
//        return "요청이 정상적으로 처리되었습니다.";
//    }
//
//    @ResponseStatus(HttpStatus.CREATED)
//    @PostMapping("/{postId}/likes")
//    @Operation(summary = "게시글 추천", description = "post-id에 해당하는 게시글에 추천을 누른다.")
//    public String likePost(@PathVariable Long postId) {
//        postService.likePost(postId);
//        return "요청이 정상적으로 처리되었습니다.";
//    }
//
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @DeleteMapping("/{postId}/likes")
//    @Operation(summary = "게시글 추천 취소", description = "post-id에 해당하는 게시글에 누른 추천을 취소한다.")
//    public String cancelLikePost(@PathVariable Long postId) {
//        postService.cancelLikePost(postId);
//        return "요청이 정상적으로 처리되었습니다.";
//    }
//
//    @ResponseStatus(HttpStatus.OK)
//    @PostMapping("/{postId}/report")
//    @Operation(summary = "게시글 신고", description = "post-id에 해당하는 게시글을 신고 처리한다.")
//    public String reportPost(@PathVariable Long postId, @RequestBody ReportRequest request) {
//        postService.reportPost(postId, request);
//        return "신고가 성공적으로 접수되었습니다.";
//    }
}
