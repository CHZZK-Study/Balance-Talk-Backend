package balancetalk.bookmark.presentation;

import balancetalk.bookmark.application.BookmarkGameService;
import balancetalk.global.utils.AuthPrincipal;
import balancetalk.member.dto.ApiMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmarks/game-sets/{gameSetId}")
@Tag(name = "bookmark", description = "북마크 API")
public class BookmarkGameController {

    private final BookmarkGameService bookmarkGameService;

    @Operation(summary = "밸런스게임 세트 북마크 추가", description = "밸런스게임 세트에 북마크를 추가합니다.")
    @PostMapping("/games/{gameId}")
    public void bookmarkGameSet(@PathVariable final Long gameSetId, @PathVariable final Long gameId,
                                @Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {
        bookmarkGameService.createBookmark(gameSetId, gameId, apiMember);
    }

    @Operation(summary = "투표 완료한 밸런스게임 세트 북마크 추가", description = "전체 투표를 완료한 밸런스게임 세트에 북마크를 추가합니다. ")
    @PostMapping()
    public void bookmarkEndGameSet(@PathVariable final Long gameSetId,
                                   @Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {
        bookmarkGameService.createEndGameSetBookmark(gameSetId, apiMember);
    }

    @Operation(summary = "밸런스게임 세트 북마크 취소", description = "밸런스게임 세트에 북마크를 취소합니다.")
    @DeleteMapping
    public void deleteBookmarkGame(@PathVariable final Long gameSetId,
                                   @Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {
        bookmarkGameService.deleteBookmark(gameSetId, apiMember);
    }
}
