package balancetalk.game.dto;

import balancetalk.file.domain.File;
import balancetalk.file.domain.repository.FileRepository;
import balancetalk.game.domain.GameOption;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.vote.domain.VoteOption;
import com.fasterxml.jackson.annotation.JsonInclude;
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

    @Schema(description = "선택지 이미지",
            example = "https://pikko-image.s3.ap-northeast-2.amazonaws.com/balance-game/4839036ee7cd_unnamed.png")
    private String imgUrl;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "선택지 이미지 파일 ID", example = "12")
    private Long fileId;

    @Schema(description = "선택지 추가설명", example = "선택지 추가 설명")
    private String description;

    @Schema(description = "선택지", example = "A")
    private VoteOption optionType;

    public static GameOptionDto fromEntity(GameOption gameOption, String imgUrl) {
        return GameOptionDto.builder()
                .id(gameOption.getId())
                .name(gameOption.getName())
                .imgUrl(imgUrl)
                .description(gameOption.getDescription())
                .optionType(gameOption.getOptionType())
                .build();
    }

    public GameOption toEntity(FileRepository fileRepository) {
        if (fileId != null) {
            File file = fileRepository.findById(fileId)
                    .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_FILE));
        }
        return GameOption.builder()
                .name(name)
                .imgId(fileId)
                .description(description)
                .optionType(optionType)
                .build();
    }
}
