package balancetalk.talkpick.presentation;

import balancetalk.global.utils.AuthPrincipal;
import balancetalk.member.dto.ApiMember;
import balancetalk.talkpick.application.TempTalkPickService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static balancetalk.talkpick.dto.TempTalkPickDto.FindTempTalkPickResponse;
import static balancetalk.talkpick.dto.TempTalkPickDto.SaveTempTalkPickRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/talks/temp")
@Tag(name = "talk_pick", description = "톡픽 API")
public class TempTalkPickController {

    private final TempTalkPickService tempTalkPickService;

    @Operation(summary = "톡픽 임시 저장", description = "작성중인 톡픽을 임시 저장합니다.")
    @PostMapping
    public void saveTempTalkPick(@RequestBody @Valid final SaveTempTalkPickRequest request,
                                 @Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {
        tempTalkPickService.createTempTalkPick(request, apiMember);
    }

    @Operation(summary = "임시 저장한 톡픽 조회", description = "임시 저장한 톡픽을 불러옵니다.")
    @GetMapping
    public FindTempTalkPickResponse findTempTalkPick(@Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {
        return tempTalkPickService.findTempTalkPick(apiMember);
    }
}
