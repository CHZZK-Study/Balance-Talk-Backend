package balancetalk.talkpick.presentation;

import balancetalk.global.common.ApiResponse;
import balancetalk.talkpick.dto.CreateTalkPickRequest;
import balancetalk.talkpick.dto.CreateTalkPickResponse;
import balancetalk.talkpick.dto.TodayTalkPickResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/talks")
@Tag(name = "talk_pick", description = "톡픽 API")
public class TalkPickController {

    @Operation(summary = "톡픽 생성", description = "톡픽을 생성합니다.")
    @PostMapping
    public ApiResponse<CreateTalkPickResponse> createTalkPick(@RequestBody final CreateTalkPickRequest request) {
        return ApiResponse.ok(new CreateTalkPickResponse(
                1L,
                request.getTitle(),
                request.getContent(),
                request.getSummary(),
                0L,
                0L,
                request.getOptionA(),
                request.getOptionB()));
    }

    @Operation(summary = "오늘의 톡픽 조회 (메인)", description = "메인 페이지에서 오늘의 톡픽을 조회합니다.")
    @GetMapping("/today")
    public ApiResponse<TodayTalkPickResponse> findTodayTalkPick(final Pageable pageable) {
        return ApiResponse.ok(new TodayTalkPickResponse(1L, "제목", "요약", "O", "X"));
    }
}
