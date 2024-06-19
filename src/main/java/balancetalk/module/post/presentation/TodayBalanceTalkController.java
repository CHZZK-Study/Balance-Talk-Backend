package balancetalk.module.post.presentation;

import balancetalk.module.post.dto.BalanceGameResponse;
import balancetalk.module.post.dto.TodayBalanceTalkResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/games")
@Tag(name = "game", description = "오늘의 밸런스톡 조회 API")
public class TodayBalanceTalkController {

    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping("/today")
    @Operation(summary = "오늘의 밸런스톡 조회", description = "메인 페이지에서 오늘의 밸런스톡을 조회한다.")
    public TodayBalanceTalkResponse findTodayBalanceTalk() {
        return null;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping()
    @Operation(summary = "밸런스 게임 목록 조회", description = "메인 페이지에서 밸런스 게임들을 조회한다.")
    public Page<BalanceGameResponse> findAllGames(@Parameter(name = "tag", description = "태그",
                                                            example = "all", required = true)
                                                      @RequestParam(value = "tag", required = false) String tag,
                                                    @Parameter(name = "page", description = "페이지 번호",
                                                                example = "0", required = true)
                                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                                    @Parameter(name = "size", description = "페이지 크기",
                                                                example = "10", required = true)
                                                        @RequestParam(value = "size", defaultValue = "10") int size) {
        return null;

    }
}
