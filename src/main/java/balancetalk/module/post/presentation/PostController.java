package balancetalk.module.post.presentation;

import balancetalk.module.post.application.PostService;
import balancetalk.module.post.dto.PostRequestDto;
import balancetalk.module.post.dto.PostResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "post", description = "게시글 API")
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "게시글 생성" , description = "로그인 상태인 회원이 게시글을 작성한다.")
    public PostResponseDto createPost(@RequestBody final PostRequestDto postRequestDto) {
        return postService.save(postRequestDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @Operation(summary = "모든 게시글 조회", description = "해당 회원이 쓴 모든 글을 조회한다.")
    public List<PostResponseDto> findAllPost() {
        return postService.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{postId}")
    @Operation(summary = "게시글 조회", description = "post-id에 해당하는 게시글을 조회한다.")
    public PostResponseDto findSinglePost(@PathVariable("postId") Long postId) {
        return postService.findById(postId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @DeleteMapping("/{postId}")
    @Operation(summary = "게시글 삭제", description = "post-id에 해당하는 게시글을 삭제한다.")
    public String deletePost(@PathVariable("postId") Long postId) {
        postService.deleteById(postId);
        return "요청이 정상적으로 처리되었습니다.";
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{postId}/likes")
    @Operation(summary = "게시글 추천", description = "post-id에 해당하는 게시글에 추천을 누른다.")
    public ResponseEntity<String> likePost(@PathVariable Long postId, @RequestBody Long memberId) {
        Long likedPostId = postService.likePost(postId, memberId);
        String contentLocation = "/posts/" + likedPostId;
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Content-Location", contentLocation)
                .body("요청이 정상적으로 처리되었습니다.");
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{postId}/likes")
    public String cancelLikePost(@PathVariable Long postId, @RequestBody Long memberId) {
        postService.cancelLikePost(postId, memberId);
        return "요청이 정상적으로 처리되었습니다.";
    }
}
