package balancetalk.module.post.presentation;

import balancetalk.module.post.application.PostService;
import balancetalk.module.post.dto.PostRequestDto;
import balancetalk.module.post.dto.PostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public String createPost(@RequestBody final PostRequestDto postRequestDto) {
        postService.save(postRequestDto);
        return "게시글이 등록 되었습니다.";
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<PostResponseDto> findAllPost() {
        return postService.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{postId}")
    public PostResponseDto findSinglePost(@PathVariable("postId") Long postId) {
        return postService.findById(postId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @DeleteMapping("/{postId}")
    public String deletePost(@PathVariable("postId") Long postId) {
        postService.deleteById(postId);
        return "요청이 정상적으로 처리되었습니다.";
    }
}
