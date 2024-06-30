package balancetalk.game.presentation;

import balancetalk.game.dto.GameRequest;
import balancetalk.game.dto.GameResponse;
import balancetalk.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("games")
@Tag(name = "game", description = "게임 API")
public class GameController {

    @PostMapping
    @Operation(summary = "게임 생성", description = "게임을 생성합니다.")
    public ApiResponse<GameResponse> createGame(@RequestBody final GameRequest request) {
        return ApiResponse.ok(new GameResponse(1L, request.getTitle(), request.getOptionA(), request.getOptionB()));
    }

    @GetMapping("/{gameId}")
    @Operation(summary = "게임 조회", description = "게임을 조회합니다.")
    public ApiResponse<GameResponse> findGame(@PathVariable final Long gameId) {
        return ApiResponse.ok(new GameResponse(1L, "제목", "O", "X"));
    }

    @PutMapping("/{gameId}")
    @Operation(summary = "게임 수정", description = "게임을 수정합니다.")
    public ApiResponse<GameResponse> updateGame(@PathVariable final Long gameId, @RequestBody final GameRequest request) {
        return ApiResponse.ok(new GameResponse(1L, "변경된 제목", "O", "X"));
    }

    @DeleteMapping("/{gameId}")
    @Operation(summary = "게임 삭제", description = "게임을 삭제합니다.")
    public ApiResponse<Object> deleteGame(@PathVariable final Long gameId) {
        return ApiResponse.ok();
    }

    @GetMapping("/best")
    @Operation(summary = "인기 게임 조회", description = "인기 있는 게임 목록을 조회합니다.")
    public ApiResponse<List<GameResponse>> findBestPosts() {
        GameResponse gameResponse1 = new GameResponse(1L, "제목1", "O", "X");
        GameResponse gameResponse2 = new GameResponse(2L, "제목2", "X", "O");
        List<GameResponse> gameResponses = Arrays.asList(gameResponse1, gameResponse2);
        return ApiResponse.ok(gameResponses);
    }

    @GetMapping("/new")
    @Operation(summary = "새로운 게임 조회", description = "새로 업로드 된 게임 목록들을 조회합니다.")
    public ApiResponse<List<GameResponse>> findNewPosts() {
        GameResponse gameResponse1 = new GameResponse(1L, "제목1", "O", "X");
        GameResponse gameResponse2 = new GameResponse(2L, "제목2", "X", "O");
        List<GameResponse> gameResponses = Arrays.asList(gameResponse1, gameResponse2);
        return ApiResponse.ok(gameResponses);
    }
}
