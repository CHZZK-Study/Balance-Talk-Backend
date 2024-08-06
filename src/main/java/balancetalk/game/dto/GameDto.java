package balancetalk.game.dto;

import balancetalk.game.domain.Game;
import balancetalk.game.domain.GameTopic;
import balancetalk.member.domain.Member;
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

        @Schema(description = "선택지 A 이미지", example = "https://pikko-image.s3.ap-northeast-2.amazonaws.com/balance-game/067cc56e-21b7-468f-a2c1-4839036ee7cd_unnamed.png")
        private String optionAImg;

        @Schema(description = "선택지 B 이름", example = "선택지 B 이름")
        private String optionB;

        @Schema(description = "선택지 B 이미지", example = "https://pikko-image.s3.ap-northeast-2.amazonaws.com/balance-game/1157461e-a685-42fd-837e-7ed490894ca6_unnamed.png")
        private String optionBImg;

        @Schema(description = "밸런스 게임 주제", example = "커플")
        private String gameTopic;

        public Game toEntity(GameTopic topic, Member member) {
            return Game.builder()
                    .title(title)
                    .optionA(optionA)
                    .optionAImg(optionAImg)
                    .optionB(optionB)
                    .optionBImg(optionBImg)
                    .gameTopic(topic)
                    .member(member)
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

        @Schema(description = "선택지 A 이미지", example = "https://pikko-image.s3.ap-northeast-2.amazonaws.com/balance-game/067cc56e-21b7-468f-a2c1-4839036ee7cd_unnamed.png")
        private String optionAImg;

        @Schema(description = "선택지 A 이름", example = "선택지 A 이름")
        private String optionA;

        @Schema(description = "선택지 B 이름", example = "선택지 B 이름")
        private String optionB;

        @Schema(description = "선택지 B 이미지", example = "https://pikko-image.s3.ap-northeast-2.amazonaws.com/balance-game/1157461e-a685-42fd-837e-7ed490894ca6_unnamed.png")
        private String optionBImg;
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

        @Schema(description = "선택지 A 이미지", example = "https://pikko-image.s3.ap-northeast-2.amazonaws.com/balance-game/067cc56e-21b7-468f-a2c1-4839036ee7cd_unnamed.png")
        private String optionAImg;

        @Schema(description = "선택지 B 이름", example = "선택지 B 이름")
        private String optionB;

        @Schema(description = "선택지 B 이미지", example = "https://pikko-image.s3.ap-northeast-2.amazonaws.com/balance-game/1157461e-a685-42fd-837e-7ed490894ca6_unnamed.png")
        private String optionBImg;

        @Schema(description = "조회수", example = "3")
        private long views;

        @Schema(description = "북마크 여부", example = "false")
        private Boolean myBookmark;

        @Schema(description = "투표한 선택지", example = "A")
        private VoteOption votedOption;

        @Schema(description = "밸런스 게임 주제", example = "커플")
        private String gameTopic;

        public static GameDetailResponse from(Game game, boolean myBookmark, VoteOption votedOption) {
            return GameDetailResponse.builder()
                    .id(game.getId())
                    .title(game.getTitle())
                    .optionA(game.getOptionA())
                    .optionAImg(game.getOptionAImg())
                    .optionB(game.getOptionB())
                    .optionBImg(game.getOptionBImg())
                    .views(game.getViews())
                    .myBookmark(myBookmark)
                    .votedOption(votedOption)
                    .gameTopic(game.getGameTopic().getName())
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
