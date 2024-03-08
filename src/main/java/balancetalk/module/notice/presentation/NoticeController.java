package balancetalk.module.notice.presentation;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.module.notice.application.NoticeService;
import balancetalk.module.notice.dto.NoticeRequest;
import balancetalk.module.notice.dto.NoticeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static balancetalk.global.exception.ErrorCode.PAGE_NUMBER_ZERO;
import static balancetalk.global.exception.ErrorCode.PAGE_SIZE_ZERO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notices")
@Tag(name = "notice", description = "공지사항 API")
public class NoticeController {

    private final NoticeService noticeService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "공지사항 생성" , description = "로그인 상태인 관리자가 게시글을 작성한다.")
    public NoticeResponse createNotice(@Valid @RequestBody final NoticeRequest noticeRequest) {
        return noticeService.createNotice(noticeRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @Operation(summary = "전체 공지사항 조회" , description = "작성된 모든 공지사항을 페이지네이션을 이용해 조회한다.")
    public Page<NoticeResponse> findAllNotices(@RequestParam(value = "page", defaultValue = "1") int page,
                                               @RequestParam(required = false, value = "size", defaultValue = "13") int size) {

        if (page <= 0) {
            throw new BalanceTalkException(PAGE_NUMBER_ZERO);
        }
        if (size <= 0) {
            throw new BalanceTalkException(PAGE_SIZE_ZERO);
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        return noticeService.findAllNotices(pageable);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{noticeId}")
    @Operation(summary = "특정 공지사항 조회" , description = "특정 공지사항을 조회한다.")
    public NoticeResponse findNoticeById(@PathVariable Long noticeId) {
        return noticeService.findNoticeById(noticeId);
    }

}
