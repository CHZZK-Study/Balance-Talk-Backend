package balancetalk.module.notice.presentation;

import balancetalk.module.notice.application.NoticeService;
import balancetalk.module.notice.dto.NoticeRequest;
import balancetalk.module.notice.dto.NoticeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notices")
@Tag(name = "notice", description = "공지사항 API")
public class NoticeController {

    private final NoticeService noticeService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "공지사항 생성" , description = "로그인 상태인 관리자가 게시글을 작성한다.")
    public NoticeResponse createNotice(@RequestBody final NoticeRequest noticeRequest) {
        return noticeService.createNotice(noticeRequest);
    }
}
