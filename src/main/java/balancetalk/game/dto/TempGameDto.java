package balancetalk.game.dto;

import balancetalk.game.domain.TempGame;
import balancetalk.game.domain.TempGameOption;
import balancetalk.member.domain.Member;
import balancetalk.vote.domain.VoteOption;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class TempGameDto {

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "임시 밸런스 게임 옵션 저장 요청")
    public static class TempGameOptionDto {
        @Schema(description = "선택지 이름", example = "선택지 이름")
        private String name;

        @Schema(description = "선택지 이미지", example = "https://pikko-image.s3.ap-northeast-2.amazonaws.com/balance-game/067cc56e-21b7-468f-a2c1-4839036ee7cd_unnamed.png")
        private String imgUrl;

        @Schema(description = "선택지 추가설명", example = "선택지 추가 설명")
        private String description;

        @Schema(description = "선택지", example = "A")
        private VoteOption optionType;

        public TempGameOption toEntity() {
            return TempGameOption.builder()
                    .name(name)
                    .imgUrl(imgUrl)
                    .description(description)
                    .optionType(optionType)
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "임시 밸런스 게임 저장 요청")
    public static class CreateTempGameRequest {

        @Schema(description = "제목", example = "제목")
        private String title;

        @Schema(description = "게임 추가 설명", example = "추가 설명")
        private String description;

        private List<TempGameOptionDto> tempGameOptions;

        @Schema(description = "첨부한 이미지 고유 이름 목록",
                example = "[" +
                        "\"9b4856fe-b624-4e54-ad80-a94e083301d2_czz.png\",\n" +
                        "\"fdcbd97b-f9be-45d1-b855-43f3fd17d5a6_6d588490-d5d4-4e47-b5d0-957e6ed4830b_prom.jpeg\"" +
                        "]")
        private List<String> storedNames;

        public TempGame toEntity(Member member) {
            return TempGame.builder()
                    .member(member)
                    .title(title)
                    .description(description)
                    .tempGameOptions(tempGameOptions.stream().map(TempGameOptionDto::toEntity).toList())
                    .bookmarks(0L)
                    .build();
        }
    }
}
