package balancetalk.vote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VoteResponse {

    @Schema(description = "선택지 B 투표 개수", example = "23")
    private int optionACount;

    @Schema(description = "선택지 B 투표 개수", example = "12")
    private int optionBCount;
}
