package balancetalk.vote.presentation;

import balancetalk.global.utils.AuthPrincipal;
import balancetalk.member.dto.ApiMember;
import balancetalk.member.dto.GuestOrApiMember;
import balancetalk.vote.application.VoteGameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static balancetalk.vote.dto.VoteGameDto.VoteRequest;

@RestController
@RequestMapping("/votes/games/{gameId}")
@RequiredArgsConstructor
@Tag(name = "vote", description = "투표 API")
public class VoteGameController {

    private final VoteGameService voteGameService;

    @Operation(summary = "밸런스 게임 투표 생성", description = "밸런스 게임에서 원하는 선택지에 투표합니다.")
    @PostMapping
    public void createVoteGame(@PathVariable Long gameId, @RequestBody VoteRequest request,
                               @Parameter(hidden = true) @AuthPrincipal GuestOrApiMember guestOrApiMember) {
        voteGameService.createVote(gameId, request, guestOrApiMember);
    }

    @Operation(summary = "밸런스 게임 투표 수정", description = "밸런스 게임 투표를 수정합니다.")
    @PutMapping
    public void updateVoteGame(@PathVariable Long gameId, @RequestBody VoteRequest request,
                               @Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {
        voteGameService.updateVote(gameId, request, apiMember);
    }

    @Operation(summary = "밸런스 게임 투표 삭제", description = "밸런스 게임 투표를 삭제합니다.")
    @DeleteMapping
    public void deleteVoteGame(@PathVariable Long gameId,
                               @Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {
        voteGameService.deleteVote(gameId, apiMember);
    }
}
