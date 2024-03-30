package balancetalk.module.report.dto;

import balancetalk.module.report.domain.ReportCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ReportRequest {

    @Schema(description = "신고 이유", example = "정치 게시글 / 분란성 댓글")
    private ReportCategory category;

    @Schema(description = "신고 설명", example = "게시글 / 댓글 신고한 이유")
    private String description;

}
