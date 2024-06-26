package balancetalk.vote.presentation;

import balancetalk.vote.dto.VoteRequest;
import balancetalk.vote.dto.VoteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/talks/{talkPickId}/vote")
@Tag(name = "vote", description = "투표 API")
public class VoteTalkPickController {

    private static final String SUCCESS_RESPONSE_MESSAGE = "OK";

    @Operation(summary = "톡픽 투표 생성", description = "톡픽에서 원하는 선택지에 투표합니다.")
    @PostMapping
    public String createVoteTalkPick(@PathVariable Long talkPickId,
                                     @RequestBody VoteRequest request) {
        return SUCCESS_RESPONSE_MESSAGE;
    }

    @Operation(summary = "톡픽 투표 결과 조회", description = "톡픽 투표 결과를 조회합니다.")
    @GetMapping
    public VoteResponse getVoteResultTalkPick(@PathVariable Long talkPickId) {
        return new VoteResponse(23, 12);
    }

    @Operation(summary = "톡픽 투표 수정", description = "톡픽 투표를 수정합니다.")
    @PutMapping
    public String updateVoteResultTalkPick(@PathVariable Long talkPickId,
                                           @RequestBody VoteRequest request) {
        return SUCCESS_RESPONSE_MESSAGE;
    }
}
