package balancetalk.vote.presentation;

import balancetalk.vote.dto.VoteGameDto.VoteResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import static balancetalk.vote.dto.VoteGameDto.VoteRequest;

@RestController
@RequestMapping("/votes/games/{gameId}")
@Tag(name = "vote", description = "투표 API")
public class VoteGameController {

    @Operation(summary = "밸런스 게임 투표 생성", description = "밸런스 게임에서 원하는 선택지에 투표합니다.")
    @PostMapping
    public void createVoteGame(@PathVariable Long gameId, @RequestBody VoteRequest request) {
    }

    @Operation(summary = "밸런스 게임 투표 결과 조회", description = "밸런스 게임 투표 결과를 조회합니다.")
    @GetMapping
    public VoteResultResponse getVoteResultGame(@PathVariable Long gameId) {
        return new VoteResultResponse(23, 12);
    }

    @Operation(summary = "밸런스 게임 투표 수정", description = "밸런스 게임 투표를 수정합니다.")
    @PutMapping
    public void updateVoteResultGame(@PathVariable Long gameId, @RequestBody VoteRequest request) {
    }
}
