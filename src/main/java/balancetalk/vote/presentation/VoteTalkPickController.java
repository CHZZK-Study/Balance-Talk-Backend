package balancetalk.vote.presentation;

import balancetalk.global.common.ApiResponse;
import balancetalk.vote.dto.VoteRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/talks/{talkPickId}/vote")
@Tag(name = "vote", description = "투표 API")
public class VoteTalkPickController {

    @Operation(summary = "톡픽 투표", description = "톡픽에서 원하는 선택지에 투표합니다.")
    @PostMapping
    public ApiResponse<Object> voteTalkPick(@PathVariable Long talkPickId,
                                            @RequestBody VoteRequest request) {
        return ApiResponse.ok();
    }
}
