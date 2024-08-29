package balancetalk.talkpick.presentation;

import balancetalk.global.utils.AuthPrincipal;
import balancetalk.member.dto.ApiMember;
import balancetalk.talkpick.application.SummaryContentService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/talks/{talkPickId}/summary")
@RequiredArgsConstructor
public class SummaryContentController {

    private final SummaryContentService summaryContentService;

    @PostMapping
    public void summaryContent(@PathVariable final Long talkPickId,
                               @Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {
        summaryContentService.summaryContent(talkPickId, apiMember);
    }
}
