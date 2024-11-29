package balancetalk.game.dto;

import balancetalk.file.domain.repository.FileRepository;
import balancetalk.game.domain.TempGameOption;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.vote.domain.VoteOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
@Schema(description = "임시 밸런스 게임 선택지")
public class TempGameOptionDto {

    @Schema(description = "선택지 이름", example = "선택지 이름")
    private String name;

    @Schema(description = "선택지 이미지 파일 ID", example = "12")
    private Long fileId;

    @Schema(description = "선택지 추가설명", example = "선택지 추가 설명")
    private String description;

    @Schema(description = "선택지", example = "A")
    private VoteOption optionType;

    public TempGameOption toEntity(FileRepository fileRepository) {
        if (fileId != null && !fileRepository.existsById(fileId)) {
            throw new BalanceTalkException(ErrorCode.NOT_FOUND_FILE);
        }

        return TempGameOption.builder()
                .name(name)
                .imgId(fileId)
                .description(description)
                .optionType(optionType)
                .build();
    }

    public static TempGameOptionDto fromEntity(TempGameOption tempGameOption, Long fileId) {

        return TempGameOptionDto.builder()
                .name(tempGameOption.getName())
                .fileId(fileId)
                .description(tempGameOption.getDescription())
                .optionType(tempGameOption.getOptionType())
                .build();
    }
}