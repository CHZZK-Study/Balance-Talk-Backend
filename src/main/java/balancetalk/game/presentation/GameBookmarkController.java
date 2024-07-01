package balancetalk.game.presentation;

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
public class GameBookmarkController {

    private static final String SUCCESS_RESPONSE_MESSAGE = "OK";

    @Operation(summary = "밸런스 게임 북마크", description = "밸런스 게임 북마크를 활성화합니다.")
    @PostMapping
    public String bookmarkGame(@PathVariable final Long gameId) {
        return SUCCESS_RESPONSE_MESSAGE;
    }

    @Operation(summary = "밸런스 게임 북마크 취소", description = "밸런스 게임 북마크를 취소합니다.")
    @DeleteMapping
    public String deleteBookmarkGame(@PathVariable final Long gameId) {
        return SUCCESS_RESPONSE_MESSAGE;
    }
}
