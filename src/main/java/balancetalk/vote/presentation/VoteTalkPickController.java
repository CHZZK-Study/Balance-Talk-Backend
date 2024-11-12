package balancetalk.vote.presentation;

import static balancetalk.vote.dto.VoteTalkPickDto.VoteRequest;

import balancetalk.global.utils.AuthPrincipal;
import balancetalk.member.dto.ApiMember;
import balancetalk.vote.application.VoteTalkPickService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/votes/talks/{talkPickId}")
@Tag(name = "vote", description = "투표 API")
public class VoteTalkPickController {

    private final VoteTalkPickService voteTalkPickService;

    @Operation(summary = "톡픽 투표 생성", description = "톡픽에서 원하는 선택지에 투표합니다.")
    @PostMapping
    public void createVoteTalkPick(@PathVariable long talkPickId,
                                   @RequestBody @Valid VoteRequest request,
                                   @Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {
        voteTalkPickService.createVote(talkPickId, request, apiMember);
    }

    @Operation(summary = "톡픽 투표 수정", description = "톡픽 투표를 수정합니다.")
    @PutMapping
    public void updateVoteResultTalkPick(@PathVariable long talkPickId,
                                         @RequestBody @Valid VoteRequest request,
                                         @Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {
        voteTalkPickService.updateVote(talkPickId, request, apiMember);
    }

    @Operation(summary = "톡픽 투표 삭제", description = "톡픽 투표를 삭제합니다.")
    @DeleteMapping
    public void deleteVoteTalkPick(@PathVariable long talkPickId,
                                   @Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {
        voteTalkPickService.deleteVote(talkPickId, apiMember);
    }
}
