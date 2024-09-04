package balancetalk.talkpick.dto;

import balancetalk.talkpick.domain.Summary;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "톡픽 3줄 요약")
@Data
public class SummaryResponse {

    @Schema(description = "요약 1번째 줄", example = "요약 1번째 줄 내용")
    private String summaryFirstLine;

    @Schema(description = "요약 2번째 줄", example = "요약 2번째 줄 내용")
    private String summarySecondLine;

    @Schema(description = "요약 3번째 줄", example = "요약 3번째 줄 내용")
    private String summaryThirdLine;

    @QueryProjection
    public SummaryResponse(Summary summary) {
        this.summaryFirstLine = summary.getFirstLine();
        this.summarySecondLine = summary.getSecondLine();
        this.summaryThirdLine = summary.getThirdLine();
    }
}
