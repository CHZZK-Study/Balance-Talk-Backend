package balancetalk.game.dto;

import balancetalk.game.domain.GameOption;
import balancetalk.vote.domain.VoteOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "밸런스 게임 선택지")
public class GameOptionDto {

    private Long id;

    @Schema(description = "선택지 이름", example = "선택지 이름")
    private String name;

    @Schema(description = "선택지 이미지", example = "https://pikko-image.s3.ap-northeast-2.amazonaws.com/balance-game/067cc56e-21b7-468f-a2c1-4839036ee7cd_unnamed.png")
    private String imgUrl;

    private String storedName;

    @Schema(description = "선택지 추가설명", example = "선택지 추가 설명")
    private String description;

    @Schema(description = "선택지", example = "A")
    private VoteOption optionType;

    public GameOption toEntity() {
        return GameOption.builder()
                .name(name)
                .imgUrl(imgUrl)
                .description(description)
                .optionType(optionType)
                .build();
    }

    public static GameOptionDto fromEntity(GameOption gameOption) {
        return GameOptionDto.builder()
                .id(gameOption.getId())
                .name(gameOption.getName())
                .imgUrl(gameOption.getImgUrl())
                .description(gameOption.getDescription())
                .optionType(gameOption.getOptionType())
                .build();
    }
}
