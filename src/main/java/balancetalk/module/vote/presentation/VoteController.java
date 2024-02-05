package balancetalk.module.vote.presentation;

import balancetalk.module.vote.application.VoteService;
import balancetalk.module.vote.dto.VoteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/posts/{post-id}/vote")
    public void vote(@PathVariable("post-id") Long postId, @RequestBody VoteRequest voteRequest) {
        voteService.createVote(voteRequest);
    }
}
