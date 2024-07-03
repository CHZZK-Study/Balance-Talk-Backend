package balancetalk.vote.dto;

import balancetalk.vote.domain.VoteOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

public class VoteDto {

    @Data
    public static class VoteRequest {

        @Schema(description = "투표할 선택지", example = "A")
        private VoteOption voteOption;
    }

    @Data
    @AllArgsConstructor
    public static class VoteResponse {

        @Schema(description = "선택지 B 투표 개수", example = "23")
        private int optionACount;

        @Schema(description = "선택지 B 투표 개수", example = "12")
        private int optionBCount;
    }
}
