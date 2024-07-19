package balancetalk.vote.dto;

import balancetalk.vote.domain.VoteOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

public class VoteTalkPickDto {

    @Data
    @AllArgsConstructor
    @Schema(description = "톡픽 투표 생성 요청")
    public static class VoteRequest {

        @Schema(description = "투표할 선택지", example = "A")
        private VoteOption voteOption;
    }

    @Data
    @AllArgsConstructor
    @Schema(description = "톡픽 투표 결과 응답")
    public static class VoteResultResponse {

        @Schema(description = "선택지 B 투표 개수", example = "23")
        private int optionACount;

        @Schema(description = "선택지 B 투표 개수", example = "12")
        private int optionBCount;
    }
}
