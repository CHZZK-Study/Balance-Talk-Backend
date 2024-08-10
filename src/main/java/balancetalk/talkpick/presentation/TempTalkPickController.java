package balancetalk.talkpick.presentation;

import balancetalk.global.utils.AuthPrincipal;
import balancetalk.member.dto.ApiMember;
import balancetalk.talkpick.application.TempTalkPickService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static balancetalk.talkpick.dto.TempTalkPickDto.Request;

@RestController
@RequiredArgsConstructor
@RequestMapping("/talks/temp")
@Tag(name = "talk_pick", description = "톡픽 API")
public class TempTalkPickController {

    private final TempTalkPickService tempTalkPickService;

    @Operation(summary = "톡픽 임시 저장", description = "작성중인 톡픽을 임시 저장합니다.")
    @PostMapping
    public void saveTempTalkPick(@RequestBody final Request request,
                                 @Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {
        tempTalkPickService.createTempTalkPick(request, apiMember);
    }
}
