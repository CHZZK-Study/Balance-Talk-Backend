package balancetalk.module.vote.presentation;

import static org.springframework.http.HttpStatus.*;

import balancetalk.module.vote.application.VoteService;
import balancetalk.module.vote.dto.VoteRequest;
import balancetalk.module.vote.dto.VotingStatusResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{post-id}/vote")
public class VoteController {

    private final VoteService voteService;

    @ResponseStatus(CREATED)
    @PostMapping
    public String vote(@PathVariable("post-id") Long postId, @RequestBody VoteRequest voteRequest) {
        voteService.createVote(voteRequest);
        return "투표가 정상적으로 처리되었습니다.";
    }

    @ResponseStatus(OK)
    @GetMapping
    public List<VotingStatusResponse> votingStatus(@PathVariable("post-id") Long postId) {
        return voteService.readVotingStatus(postId);
    }
}
