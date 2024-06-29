package balancetalk.vote.presentation;

import balancetalk.global.common.ApiResponse;
import balancetalk.vote.dto.VoteRequest;
import balancetalk.vote.dto.VoteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/talks/{talkPickId}/vote")
@Tag(name = "vote", description = "투표 API")
public class VoteTalkPickController {

    @Operation(summary = "톡픽 투표 생성", description = "톡픽에서 원하는 선택지에 투표합니다.")
    @PostMapping
    public ApiResponse<Object> createVoteTalkPick(@PathVariable Long talkPickId,
                                                  @RequestBody VoteRequest request) {
        return ApiResponse.ok();
    }

    @Operation(summary = "톡픽 투표 결과 조회", description = "톡픽 투표 결과를 조회합니다.")
    @GetMapping
    public ApiResponse<VoteResponse> getVoteResultTalkPick(@PathVariable Long talkPickId) {
        return ApiResponse.ok(new VoteResponse(23, 12));
    }

    @Operation(summary = "톡픽 투표 수정", description = "톡픽 투표를 수정합니다.")
    @PutMapping
    public ApiResponse<VoteResponse> updateVoteResultTalkPick(@PathVariable Long talkPickId,
                                                              @RequestBody VoteRequest request) {
        return ApiResponse.ok();
    }
}
