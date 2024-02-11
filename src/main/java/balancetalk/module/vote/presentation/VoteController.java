package balancetalk.module.vote.presentation;

import static org.springframework.http.HttpStatus.*;

import balancetalk.module.vote.application.VoteService;
import balancetalk.module.vote.dto.VoteRequest;
import balancetalk.module.vote.dto.VotingStatusResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/vote")
public class VoteController {

    private final VoteService voteService;

    @ResponseStatus(CREATED)
    @PostMapping
    public String vote(@PathVariable Long postId, @RequestBody VoteRequest voteRequest) {
        voteService.createVote(postId, voteRequest);
        return "투표가 정상적으로 처리되었습니다.";
    }

    @ResponseStatus(OK)
    @GetMapping
    public List<VotingStatusResponse> votingStatus(@PathVariable Long postId) {
        return voteService.readVotingStatus(postId);
    }

    @ResponseStatus(OK)
    @PutMapping
    public String updateVote(@PathVariable Long postId, @RequestBody VoteRequest voteRequest) {
        voteService.updateVote(postId, voteRequest);
        return "투표가 정상적으로 변경되었습니다.";
    }
}
