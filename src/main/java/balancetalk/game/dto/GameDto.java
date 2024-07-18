package balancetalk.game.dto;

import balancetalk.game.domain.GameTopic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class GameDto {

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "밸런스 게임 생성 요청")
    public static class GameRequest {

        @Schema(description = "제목", example = "제목")
        private String title;

        @Schema(description = "선택지 A 이름", example = "선택지 A 이름")
        private String optionA;

        @Schema(description = "선택지 B 이름", example = "선택지 B 이름")
        private String optionB;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "밸런스 게임 조회 응답")
    public static class GameResponse {

        @Schema(description = "밸런스 게임 id", example = "1")
        private Long id;

        @Schema(description = "밸런스 게임 제목", example = "제목")
        private String title;

        @Schema(description = "선택지 A 이름", example = "선택지 A 이름")
        private String optionA;

        @Schema(description = "선택지 B 이름", example = "선택지 B 이름")
        private String optionB;

        @Schema(description = "총 투표 수", example = "10")
        private int totalVotes;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "밸런스 게임 상세 조회 응답")
    public static class GameDetailResponse {

        @Schema(description = "밸런스 게임 id", example = "1")
        private Long id;

        @Schema(description = "밸런스 게임 제목", example = "제목")
        private String title;

        @Schema(description = "선택지 A 이름", example = "선택지 A 이름")
        private String optionA;

        @Schema(description = "선택지 B 이름", example = "선택지 B 이름")
        private String optionB;

        @Schema(description = "북마크 여부", example = "false")
        private Boolean myBookmark;

        @Schema(description = "투표 여부", example = "false")
        private Boolean myVote;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "밸런스 게임 주제 생성")
    public static class GameTopicCreateRequest {

        @Schema(description = "밸런스 게임 주제", example = "인기")
        private String name;

        public GameTopic toEntity() {
            return GameTopic.builder()
                    .name(name)
                    .build();
        }
    }
}
