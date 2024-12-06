package balancetalk.member.presentation;

import balancetalk.game.dto.GameDto.GameMyPageResponse;
import balancetalk.global.utils.AuthPrincipal;
import balancetalk.member.application.MyPageService;
import balancetalk.member.dto.ApiMember;
import balancetalk.member.dto.MemberDto.MemberActivityResponse;
import balancetalk.talkpick.dto.TalkPickDto.TalkPickMyPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/my")
@Tag(name = "my", description = "마이페이지 API")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/talks/bookmarks")
    @Operation(summary = "북마크한 톡픽 목록 조회", description = "로그인한 회원이 북마크한 톡픽 목록을 조회한다.")
    public Page<TalkPickMyPageResponse> findAllBookmarkedTalkPicks(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6", required = false) int size,
            @Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {

        Pageable pageable = PageRequest.of(page, size);
        return myPageService.findAllBookmarkedTalkPicks(apiMember, pageable);
    }

    @GetMapping("/talks/votes")
    @Operation(summary = "투표한 톡픽 목록 조회", description = "로그인한 회원이 투표한 톡픽 목록을 조회한다.")
    public Page<TalkPickMyPageResponse> findAllVotedTalkPicks(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6", required = false) int size,
            @Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {

        Pageable pageable = PageRequest.of(page, size);
        return myPageService.findAllVotedTalkPicks(apiMember, pageable);
    }
  
    @GetMapping("/talks/comments")
    @Operation(summary = "내가 댓글 단 톡픽 목록 조회", description = "로그인한 회원이 댓글을 단 톡픽 목록을 조회한다.")
    public Page<TalkPickMyPageResponse> findAllCommentedTalkPicks(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6", required = false) int size,
            @Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {

        Pageable pageable = PageRequest.of(page, size);
        return myPageService.findAllCommentedTalkPicks(apiMember, pageable);
    }

    @GetMapping("/talks/written")
    @Operation(summary = "내가 작성한 톡픽 목록 조회", description = "로그인한 회원이 작성한 톡픽 목록을 조회한다.")
    public Page<TalkPickMyPageResponse> findAllMyTalkPicks(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6", required = false) int size,
            @Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {

        Pageable pageable = PageRequest.of(page, size);
        return myPageService.findAllTalkPicksByMember(apiMember, pageable);
    }

    @GetMapping("/game-sets/bookmarks")
    @Operation(summary = "북마크한 밸런스 게임 목록 조회", description = "로그인한 회원이 북마크한 밸런스 게임 목록을 조회한다.")
    public Page<GameMyPageResponse> findAllBookmarkedGames(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6", required = false) int size,
            @Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {

        Pageable pageable = PageRequest.of(page, size);
        return myPageService.findAllBookmarkedGames(apiMember, pageable);
    }

    @GetMapping("/game-sets/votes")
    @Operation(summary = "투표한 밸런스 게임 목록 조회", description = "로그인한 회원이 투표한 밸런스 게임 목록을 조회한다.")
    public Page<GameMyPageResponse> findAllVotedGames(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6", required = false) int size,
            @Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {

        Pageable pageable = PageRequest.of(page, size);
        return myPageService.findAllVotedGames(apiMember, pageable);
    }

    @GetMapping("/game-sets/written")
    @Operation(summary = "내가 작성한 밸런스 게임 목록 조회", description = "로그인한 회원이 작성한 밸런스 게임 목록을 조회한다.")
    public Page<GameMyPageResponse> findAllMyGames(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6", required = false) int size,
            @Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {

        Pageable pageable = PageRequest.of(page, size);
        return myPageService.findAllGamesByMember(apiMember, pageable);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/activity")
    @Operation(summary = "회원 활동 정보 조회", description = "회원 활동 정보를 조회한다.")
    public MemberActivityResponse getActivity(@Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {
        return myPageService.getMemberActivity(apiMember);
    }
}