package balancetalk.module.vote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class VotingStatusResponse {

    @Schema(description = "A 선택지 투표 개수", example = "10")
    private int optionACount;

    @Schema(description = "B 선택지 투표 개수", example = "3")
    private int optionBCount;

    @Schema(description = "총 투표 수" , example = "13")
    private int totalCount;
}
