package balancetalk.game.presentation;

import static balancetalk.game.dto.GameDto.*;
import balancetalk.game.application.GameService;
import balancetalk.global.utils.AuthPrincipal;
import balancetalk.member.dto.ApiMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/games")
@Tag(name = "game", description = "밸런스 게임 API")
public class GameTagController {

    private final GameService gameService;

    @PostMapping("/mainTag")
    @Operation(summary = "새로운 밸런스 메인 태그 생성", description = "새로운 밸런스 게임 메인 태그를 생성합니다.")
    public void createGameMainTag(@Valid @RequestBody CreateGameMainTagRequest request,
                                @Parameter(hidden = true) @AuthPrincipal final ApiMember apiMember) {
        gameService.createGameMainTag(request, apiMember);
    }
}
