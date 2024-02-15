package balancetalk.module.post.presentation;

import balancetalk.module.post.application.BookmarkService;
import balancetalk.module.post.dto.BookmarkRequestDto;
import balancetalk.module.post.dto.BookmarkResponseDto;
import balancetalk.module.post.dto.PostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("bookmark/posts")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{postId}")
    public String addBookmark(@PathVariable Long postId, @RequestBody final BookmarkRequestDto bookmarkRequestDto) {
        bookmarkService.save(bookmarkRequestDto, postId);

        return "북마크가 등록 되었습니다.";
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<BookmarkResponseDto> findAllPost() {
        return bookmarkService.findAll();
    }
}
