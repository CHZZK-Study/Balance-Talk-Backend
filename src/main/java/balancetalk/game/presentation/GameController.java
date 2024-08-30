package balancetalk.game.presentation;

import balancetalk.game.application.GameService;
import balancetalk.global.utils.AuthPrincipal;
import balancetalk.member.dto.ApiMember;
import balancetalk.member.dto.GuestOrApiMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static balancetalk.game.dto.GameDto.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/games")
@Tag(name = "game", description = "밸런스 게임 API")
public class GameController {

    private final GameService gameService;

    @PostMapping
    @Operation(summary = "밸런스 게임 생성", description = "밸런스 게임을 생성합니다.")
    public void createGame(@RequestBody final CreateGameRequest request,
                           @Parameter(hidden = true) @AuthPrincipal final ApiMember apiMember) {
        gameService.createBalanceGame(request, apiMember);
    }

    @GetMapping("/{gameId}")
    @Operation(summary = "밸런스 게임 상세 조회", description = "밸런스 게임을 조회합니다.")
    public GameDetailResponse findGame(@PathVariable final Long gameId,
                                       @Parameter(hidden = true) @AuthPrincipal final GuestOrApiMember guestOrApiMember) {
        return gameService.findBalanceGame(gameId, guestOrApiMember);
    }

    @PutMapping("/{gameId}")
    @Operation(summary = "밸런스 게임 수정", description = "밸런스 게임을 수정합니다.")
    public void updateGame(@PathVariable final Long gameId, @RequestBody final CreateGameRequest request) {
    }

    @DeleteMapping("/{gameId}")
    @Operation(summary = "밸런스 게임 삭제", description = "밸런스 게임을 삭제합니다.")
    public void deleteGame(@PathVariable final Long gameId) {

    }

    @GetMapping("/latest")
    @Operation(summary = "최신순으로 밸런스 게임 조회", description = "최신순으로 정렬된 16개의 게임 목록을 리턴합니다.")
    public List<GameResponse> findLatestGames(@RequestParam String tagName,
                                              @Parameter(hidden = true) @AuthPrincipal GuestOrApiMember guestOrApiMember) {
        return gameService.findLatestGames(tagName, guestOrApiMember);
    }

    @GetMapping("/best")
    @Operation(summary = "인기순으로 밸런스 게임 조회", description = "인기순으로 정렬된 16개의 게임 목록을 리턴합니다.")
    public List<GameResponse> findBestGames(@RequestParam String tagName,
                                            @Parameter(hidden = true)  @AuthPrincipal GuestOrApiMember guestOrApiMember) {
        return gameService.findBestGames(tagName, guestOrApiMember);
    }
}
