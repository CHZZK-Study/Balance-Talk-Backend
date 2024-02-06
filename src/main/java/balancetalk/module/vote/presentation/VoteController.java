package balancetalk.module.vote.presentation;

import static org.springframework.http.HttpStatus.*;

import balancetalk.module.vote.application.VoteService;
import balancetalk.module.vote.dto.VoteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @ResponseStatus(CREATED)
    @PostMapping("/posts/{post-id}/vote")
    public String vote(@PathVariable("post-id") Long postId, @RequestBody VoteRequest voteRequest) {
        voteService.createVote(voteRequest);
        return "투표가 정상적으로 처리되었습니다.";
    }
}
