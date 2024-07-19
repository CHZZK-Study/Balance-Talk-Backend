package balancetalk.report.presentation;

import balancetalk.comment.dto.CommentDto;
import balancetalk.global.utils.AuthPrincipal;
import balancetalk.member.dto.ApiMember;
import balancetalk.report.application.ReportCommentService;
import balancetalk.report.dto.ReportDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static balancetalk.report.dto.ReportDto.CreateReportRequest;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "report", description = "신고 API")
public class ReportController {

    private final ReportCommentService reportCommentService;

    @PostMapping("/talks/{talkPickId}/comments/{commentId}")
    @Operation(summary = "댓글 신고", description = "comment-Id에 해당하는 댓글을 신고한다.")
    public void createCommentReport(@PathVariable Long talkPickId, @PathVariable Long commentId,
                                    @Valid @RequestBody CreateReportRequest createCommentRequest,
                                    @AuthPrincipal ApiMember apiMember) {

        reportCommentService.createCommentReport(createCommentRequest, apiMember, commentId, talkPickId);
    }

}
