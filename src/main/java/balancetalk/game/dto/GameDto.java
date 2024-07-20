package balancetalk.game.dto;

import balancetalk.game.domain.Game;
import balancetalk.game.domain.GameTopic;
import balancetalk.vote.domain.VoteOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class GameDto {

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "밸런스 게임 생성 요청")
    public static class CreateGameRequest {

        @Schema(description = "제목", example = "제목")
        private String title;

        @Schema(description = "선택지 A 이름", example = "선택지 A 이름")
        private String optionA;

        @Schema(description = "선택지 B 이름", example = "선택지 B 이름")
        private String optionB;

        @Schema(description = "밸런스 게임 주제", example = "커플")
        private String name;

        public Game toEntity(GameTopic topic) {
            return Game.builder()
                    .title(title)
                    .optionA(optionA)
                    .optionB(optionB)
                    .gameTopic(topic)
                    .build();
        }
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

        @Schema(description = "조회수", example = "3")
        private Long views;

        @Schema(description = "북마크 여부", example = "false")
        private Boolean myBookmark;

        @Schema(description = "투표한 선택지", example = "A")
        private VoteOption votedOption;

        @Schema(description = "밸런스 게임 주제", example = "커플")
        private GameTopic gameTopic;

        public static GameDetailResponse from(Game game, boolean myBookmark, VoteOption votedOption) {
            return GameDetailResponse.builder()
                    .id(game.getId())
                    .title(game.getTitle())
                    .optionA(game.getOptionA())
                    .optionB(game.getOptionB())
                    .views(game.getViews())
                    .myBookmark(myBookmark)
                    .votedOption(votedOption)
                    .gameTopic(game.getGameTopic())
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "밸런스 게임 주제 생성")
    public static class CreateGameTopicRequest {

        @Schema(description = "밸런스 게임 주제", example = "커플")
        private String name;

        public GameTopic toEntity() {
            return GameTopic.builder()
                    .name(name)
                    .build();
        }
    }
}
