package balancetalk.module.vote.presentation;

import static org.springframework.http.HttpStatus.*;

import balancetalk.module.vote.application.VoteService;
import balancetalk.module.vote.dto.VoteRequest;
import balancetalk.module.vote.dto.VotingStatusResponse;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/vote")
@Tag(name = "vote", description = "선택지 투표 API")
public class VoteController {

    private final VoteService voteService;

    @ResponseStatus(CREATED)
    @PostMapping
    @Operation(summary = "선택지 투표", description = "post-id에 해당하는 게시글에서 마음에 드는 선택지에 투표한다.")
    @ApiResponse(responseCode = "201", description = "투표가 정상적으로 처리되었습니다.")
    public String createVote(@PathVariable Long postId, final @Valid @RequestBody VoteRequest voteRequest) {
        voteService.createVote(postId, voteRequest);
        return "투표가 정상적으로 처리되었습니다.";
    }

    @ResponseStatus(OK)
    @GetMapping
    @Operation(summary = "투표 현황 조회", description = "post-id에 해당하는 게시글의 투표 현황을 조회한다.")
    public List<VotingStatusResponse> votingStatus(@PathVariable Long postId) {
        return voteService.votingStatus(postId);
    }

    @ResponseStatus(OK)
    @PutMapping
    @Operation(summary = "선택지 투표 변경", description = "post-id에 해당하는 게시글에서 선택했던 투표를 변경한다.")
    public String updateVote(@PathVariable Long postId, @RequestBody VoteRequest voteRequest) {
        voteService.updateVote(postId, voteRequest);
        return "투표가 정상적으로 변경되었습니다.";
    }
}
