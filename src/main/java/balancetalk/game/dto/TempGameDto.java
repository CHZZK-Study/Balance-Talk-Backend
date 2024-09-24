package balancetalk.game.dto;

import balancetalk.game.domain.MainTag;
import balancetalk.game.domain.TempGame;
import balancetalk.game.domain.TempGameOption;
import balancetalk.game.domain.TempGameSet;
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

        private String storedName;

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

        public TempGame toEntity() {
            return TempGame.builder()
                    .title(title)
                    .description(description)
                    .tempGameOptions(tempGameOptions.stream().map(TempGameOptionDto::toEntity).toList())
                    .bookmarks(0L)
                    .build();
        }
    }

    @Data
    public static class CreateTempGameSetRequest {

        @Schema(description = "밸런스 게임 메인 태그", example = "커플")
        private String mainTag;

        @Schema(description = "밸런스 게임 서브 태그", example = "커플지옥")
        private String subTag;

        private List<CreateTempGameRequest> tempGames;

        public TempGameSet toEntity(MainTag mainTag, Member member) {
            return TempGameSet.builder()
                    .mainTag(mainTag)
                    .subTag(subTag)
                    .member(member)
                    .tempGames(tempGames.stream().map(CreateTempGameRequest::toEntity).toList())
                    .build();
        }
    }
}
