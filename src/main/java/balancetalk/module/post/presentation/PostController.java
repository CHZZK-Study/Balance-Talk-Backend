package balancetalk.module.post.presentation;

import balancetalk.module.post.application.PostService;
import balancetalk.module.post.dto.PostRequestDto;
import balancetalk.module.post.dto.PostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public void createPost(@RequestBody final PostRequestDto postRequestDto) {
        postService.save(postRequestDto);
    }

    @GetMapping
    public List<PostResponseDto> findAllPost() {
        return postService.findAll();
    }
}
