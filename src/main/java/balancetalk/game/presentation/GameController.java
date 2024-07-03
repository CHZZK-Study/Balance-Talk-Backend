package balancetalk.game.presentation;

import balancetalk.game.dto.GameDetailResponse;
import balancetalk.game.dto.GameRequest;
import balancetalk.game.dto.GameResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/games")
@Tag(name = "game", description = "밸런스 게임 API")
public class GameController {

    private static final String SUCCESS_RESPONSE_MESSAGE = "OK";

    @PostMapping
    @Operation(summary = "밸런스 게임 생성", description = "밸런스 게임을 생성합니다.")
    public GameResponse createGame(@RequestBody final GameRequest request) {
        return new GameResponse(1L, request.getTitle(), request.getOptionA(), request.getOptionB(), 10);
    }

    @GetMapping("/{gameId}")
    @Operation(summary = "밸런스 게임 상세 조회", description = "밸런스 게임을 조회합니다.")
    public GameDetailResponse findGame(@PathVariable final Long gameId) {
        return new GameDetailResponse(1L, "제목", "O", "X", false, false);
    }

    @PutMapping("/{gameId}")
    @Operation(summary = "밸런스 게임 수정", description = "밸런스 게임을 수정합니다.")
    public GameResponse updateGame(@PathVariable final Long gameId, @RequestBody final GameRequest request) {
        return new GameResponse(1L, "변경된 제목", "O", "X", 10);
    }

    @DeleteMapping("/{gameId}")
    @Operation(summary = "밸런스 게임 삭제", description = "밸런스 게임을 삭제합니다.")
    public String deleteGame(@PathVariable final Long gameId) {
        return SUCCESS_RESPONSE_MESSAGE;
    }

    @GetMapping("/best")
    @Operation(summary = "인기 밸런스 게임 조회", description = "인기 있는 밸런스 게임 목록을 조회합니다.")
    public Page<GameResponse> findBestPosts(Pageable pageable) {
        GameResponse gameResponse1 = new GameResponse(1L, "제목1", "O", "X", 10);
        GameResponse gameResponse2 = new GameResponse(2L, "제목2", "X", "O", 10);
        List<GameResponse> gameResponses = Arrays.asList(gameResponse1, gameResponse2);
        return new PageImpl<>(gameResponses, pageable, 1);
    }

    @GetMapping("/new")
    @Operation(summary = "새로운 밸런스 게임 조회", description = "새로 업로드 된 밸런스 게임 목록들을 조회합니다.")
    public List<GameResponse> findNewPosts() {
        GameResponse gameResponse1 = new GameResponse(1L, "제목1", "O", "X", 10);
        GameResponse gameResponse2 = new GameResponse(2L, "제목2", "X", "O", 10);
        return Arrays.asList(gameResponse1, gameResponse2);
    }
}
