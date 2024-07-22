package balancetalk.report.dto;

import balancetalk.member.domain.Member;
import balancetalk.report.domain.Report;
import balancetalk.report.domain.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class ReportDto {

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "신고 생성 요청")
    public static class CreateReportRequest {

        @Schema(description = "신고된 컨텐츠 타입", example = "COMMENT")
        private ReportType reportType;

        @Schema(description = "신고 사유", example = "저에게 욕설을 했습니다.")
        private String reason;

        public Report toEntity(Member reporter, Member reported, Long resourceId, String content) {
            return Report.builder()
                    .reportType(reportType)
                    .resourceId(resourceId)
                    .reason(reason)
                    .reporter(reporter)
                    .reported(reported)
                    .reportedContent(content)
                    .build();
        }
    }

    // TODO : 추후 어드민 페이지 구현시 Response 작성
}
