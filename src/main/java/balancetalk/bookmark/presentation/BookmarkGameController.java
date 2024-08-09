package balancetalk.bookmark.presentation;

import balancetalk.bookmark.application.BookmarkGameService;
import balancetalk.global.utils.AuthPrincipal;
import balancetalk.member.dto.ApiMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmarks/games/{gameId}")
@Tag(name = "bookmark", description = "북마크 API")
public class BookmarkGameController {

    private final BookmarkGameService bookmarkGameService;

    @Operation(summary = "밸런스 게임 북마크", description = "밸런스 게임 북마크를 활성화합니다.")
    @PostMapping
    public void bookmarkGame(@PathVariable final Long gameId, @AuthPrincipal ApiMember apiMember) {
        bookmarkGameService.createBookmark(gameId, apiMember);
    }

    @Operation(summary = "밸런스 게임 북마크 취소", description = "밸런스 게임 북마크를 취소합니다.")
    @DeleteMapping
    public void deleteBookmarkGame(@PathVariable final Long gameId, @AuthPrincipal ApiMember apiMember) {
        bookmarkGameService.deleteBookmark(gameId, apiMember);
    }
}
