package balancetalk.talkpick.presentation;

import balancetalk.talkpick.dto.TodayTalkPickResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/talks/today")
@Tag(name = "talk_pick", description = "톡픽 API")
public class TodayTalkPickController {

    @Operation(summary = "오늘의 톡픽 조회 (메인)", description = "메인 페이지에서 오늘의 톡픽을 조회합니다.")
    @GetMapping
    public TodayTalkPickResponse findTodayTalkPick(final Pageable pageable) {
        return new TodayTalkPickResponse(1L, "제목", "요약", "O", "X");
    }
}
