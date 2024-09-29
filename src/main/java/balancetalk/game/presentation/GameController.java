package balancetalk.game.presentation;

import balancetalk.game.application.GameService;
import balancetalk.game.dto.GameSetDto.CreateGameSetRequest;
import balancetalk.game.dto.GameSetDto.GameSetDetailResponse;
import balancetalk.game.dto.GameSetDto.GameSetResponse;
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
    @Operation(summary = "밸런스 게임 세트 생성", description = "10개 단위의 밸런스 게임을 가지고 있는 게임 세트를 생성합니다.")
    public void createGameSet(@RequestBody final CreateGameSetRequest request,
                              @Parameter(hidden = true) @AuthPrincipal final ApiMember apiMember) {
        gameService.createBalanceGameSet(request, apiMember);
    }

    @GetMapping("/{gameSetId}")
    @Operation(summary = "밸런스 게임 세트 상세 조회", description = "10개 단위의 밸런스 게임을 가지고 있는 게임 세트를 조회합니다.")
    public GameSetDetailResponse findGame(@PathVariable final Long gameSetId,
                                          @Parameter(hidden = true) @AuthPrincipal final GuestOrApiMember guestOrApiMember) {
        return gameService.findBalanceGameSet(gameSetId, guestOrApiMember);
    }

    @PutMapping("/{gameSetId}/{gameId}")
    @Operation(summary = "밸런스 게임 수정", description = "밸런스 게임을 수정합니다.")
    public void updateGame(@PathVariable final Long gameSetId, @PathVariable final Long gameId,
                           @RequestBody final CreateOrUpdateGame request,
                           @Parameter(hidden = true) @AuthPrincipal final ApiMember apiMember) {
        gameService.updateBalanceGame(gameSetId, gameId, request, apiMember);
    }

    @DeleteMapping("/{gameSetId}")
    @Operation(summary = "밸런스 게임 세트 삭제", description = "밸런스 게임 세트를 삭제합니다.")
    public void deleteGameSet(@PathVariable final Long gameSetId,
                              @Parameter(hidden = true) @AuthPrincipal final ApiMember apiMember) {
        gameService.deleteBalanceGameSet(gameSetId, apiMember);
    }

    @GetMapping("/latest")
    @Operation(summary = "최신순으로 밸런스 게임 조회", description = "최신순으로 정렬된 16개의 게임 목록을 리턴합니다.")
    public List<GameSetResponse> findLatestGames(@RequestParam String tagName) {
        return gameService.findLatestGames(tagName);
    }

    @GetMapping("/best")
    @Operation(summary = "조회수 순으로 밸런스 게임 조회", description = "조회수 순으로 정렬된 16개의 게임 목록을 리턴합니다.")
    public List<GameSetResponse> findBestGames(@RequestParam String tagName) {
        return gameService.findBestGames(tagName);
    }
}
