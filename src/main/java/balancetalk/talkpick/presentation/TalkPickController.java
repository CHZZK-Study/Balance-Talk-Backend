package balancetalk.talkpick.presentation;

import balancetalk.global.common.ApiResponse;
import balancetalk.talkpick.dto.TalkPickRequest;
import balancetalk.talkpick.dto.TalkPickResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/talks")
@Tag(name = "talk_pick", description = "톡픽 API")
public class TalkPickController {

    @Operation(summary = "톡픽 생성", description = "톡픽을 생성합니다.")
    @PostMapping
    public ApiResponse<TalkPickResponse> createTalkPick(@RequestBody final TalkPickRequest request) {
        return ApiResponse.ok(new TalkPickResponse(
                1L,
                request.getTitle(),
                request.getContent(),
                request.getSummary(),
                0L,
                0L,
                request.getOptionA(),
                request.getOptionB()));
    }
}
