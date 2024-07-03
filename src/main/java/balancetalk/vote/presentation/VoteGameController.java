package balancetalk.vote.presentation;

import balancetalk.vote.dto.VoteRequest;
import balancetalk.vote.dto.VoteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/votes/games/{gameId}")
@Tag(name = "vote", description = "투표 API")
public class VoteGameController {

    private static final String SUCCESS_RESPONSE_MESSAGE = "OK";

    @Operation(summary = "밸런스 게임 투표 생성", description = "밸런스 게임에서 원하는 선택지에 투표합니다.")
    @PostMapping
    public String createVoteGame(@PathVariable Long gameId, @RequestBody VoteRequest request) {
        return SUCCESS_RESPONSE_MESSAGE;
    }

    @Operation(summary = "밸런스 게임 투표 결과 조회", description = "밸런스 게임 투표 결과를 조회합니다.")
    @GetMapping
    public VoteResponse getVoteResultGame(@PathVariable Long gameId) {
        return new VoteResponse(23, 12);
    }

    @Operation(summary = "밸런스 게임 투표 수정", description = "밸런스 게임 투표를 수정합니다.")
    @PutMapping
    public String updateVoteResultGame(@PathVariable Long gameId, @RequestBody VoteRequest request) {
        return SUCCESS_RESPONSE_MESSAGE;
    }
}
