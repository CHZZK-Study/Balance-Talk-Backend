package balancetalk.game.presentation;

import balancetalk.game.application.TempGameService;
import balancetalk.game.dto.TempGameSetDto.CreateTempGameSetRequest;
import balancetalk.game.dto.TempGameSetDto.TempGameSetResponse;
import balancetalk.global.utils.AuthPrincipal;
import balancetalk.member.dto.ApiMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/games/temp")
@Tag(name = "game", description = "밸런스 게임 API")
public class TempGameController {

    private final TempGameService tempGameService;

    @Operation(summary = "밸런스 게임 임시 저장", description = "밸런스 게임을 임시 저장합니다.")
    @PostMapping
    public void saveTempGameSet(@RequestBody CreateTempGameSetRequest request,
                             @Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {
        tempGameService.createTempGame(request, apiMember);
    }

    @Operation(summary = "밸런스 게임 임시 저장 불러오기", description = "임시 저장된 밸런스 게임을 불러옵니다.")
    @GetMapping
    public TempGameSetResponse findTempGameSet(@Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {
        return tempGameService.findTempGameSet(apiMember);
    }
}
