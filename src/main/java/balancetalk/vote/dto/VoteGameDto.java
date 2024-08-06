package balancetalk.vote.dto;

import balancetalk.game.domain.Game;
import balancetalk.member.domain.Member;
import balancetalk.vote.domain.Vote;
import balancetalk.vote.domain.VoteOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class VoteGameDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "밸런스 게임 투표 생성 요청")
    public static class VoteRequest {

        @Schema(description = "투표할 선택지", example = "A")
        private VoteOption voteOption;

        public Vote toEntity(Member member, Game game) {
            return Vote.builder()
                    .member(member)
                    .game(game)
                    .voteOption(voteOption)
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    @Schema(description = "밸런스 게임 투표 결과 응답")
    public static class VoteResultResponse {

        @Schema(description = "선택지 B 투표 개수", example = "23")
        private int optionACount;

        @Schema(description = "선택지 B 투표 개수", example = "12")
        private int optionBCount;
    }
}
