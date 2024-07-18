package balancetalk.talkpick.dto;

import balancetalk.talkpick.domain.Summary;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "톡픽 3줄 요약")
@Data
public class SummaryDto {

    @Schema(description = "요약 1번째 줄", example = "요약 1번째 줄 내용")
    private String summaryFirstLine;

    @Schema(description = "요약 2번째 줄", example = "요약 2번째 줄 내용")
    private String summarySecondLine;

    @Schema(description = "요약 3번째 줄", example = "요약 3번째 줄 내용")
    private String summaryThirdLine;

    public SummaryDto(Summary summary) {
        this.summaryFirstLine = summary.getSummaryFirstLine();
        this.summarySecondLine = summary.getSummarySecondLine();
        this.summaryThirdLine = summary.getSummaryThirdLine();
    }
}
