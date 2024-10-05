package balancetalk.talkpick.presentation;

import balancetalk.talkpick.application.SummaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "talk_pick", description = "톡픽 API")
@RestController
@RequestMapping("/talks/{talkPickId}/summary")
@RequiredArgsConstructor
public class SummaryContentController {

    private final SummaryContentService summaryContentService;

    @Operation(summary = "톡픽 본문 요약", description = "톡픽 본문 내용을 요약합니다.")
    @PostMapping
    public void summaryContent(@PathVariable final Long talkPickId) {
        summaryContentService.summaryContent(talkPickId);
    }
}
