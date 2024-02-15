package balancetalk.module.post.presentation;

import balancetalk.module.post.application.BookmarkService;
import balancetalk.module.post.dto.BookmarkRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("bookmark/posts/{postId}")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public String addBookmark(@PathVariable Long postId, @RequestBody final BookmarkRequestDto bookmarkRequestDto) {
        bookmarkService.save(bookmarkRequestDto, postId);

        return "북마크가 등록 되었습니다.";
    }
}
