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
@RequestMapping("/bookmark")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/posts/{postId}")
    public String addBookmark(@PathVariable Long postId, @RequestBody final BookmarkRequestDto bookmarkRequestDto) {
        bookmarkService.save(bookmarkRequestDto, postId);

        return "북마크가 등록 되었습니다.";
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/members/{memberId}") // TODO: Spring Security 도입 후 현재 인증된 사용자 정보 기반으로 조회하게 변경 필요
    public List<BookmarkResponseDto> findAllPost(@PathVariable Long memberId) {
        return bookmarkService.findAllByMember(memberId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @DeleteMapping("/{bookmarkId}")
    public String deleteBookmark(@PathVariable Long bookmarkId) {
        bookmarkService.deleteById(bookmarkId);
        return "북마크가 삭제되었습니다.";
    }
}
