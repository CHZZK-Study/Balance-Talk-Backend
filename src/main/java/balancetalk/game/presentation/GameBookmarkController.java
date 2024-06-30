package balancetalk.game.presentation;

import balancetalk.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("games/{gameId}/bookmark")
@Tag(name = "bookmark", description = "북마크 API")
public class GameBookmarkController {

    @Operation(summary = "게임 북마크", description = "게임 북마크를 활성화합니다.")
    @PostMapping
    public ApiResponse<Object> bookmarkGame(@PathVariable final Long gameId) {
        return ApiResponse.ok();
    }

    @Operation(summary = "게임 북마크 취소", description = "게임 북마크를 취소합니다.")
    @DeleteMapping
    public ApiResponse<Object> deleteBookmarkGame(@PathVariable final Long gameId) {
        return ApiResponse.ok();
    }
}
