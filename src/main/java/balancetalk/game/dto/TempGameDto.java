package balancetalk.game.dto;

import balancetalk.file.domain.FileType;
import balancetalk.file.domain.repository.FileRepository;
import balancetalk.game.domain.TempGame;
import balancetalk.game.domain.TempGameOption;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class TempGameDto {

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "임시 밸런스 게임 저장 요청")
    public static class CreateTempGameRequest {

        @Schema(description = "게임 추가 설명", example = "추가 설명")
        private String description;

        private List<TempGameOptionDto> tempGameOptions;

        public TempGame toEntity(FileRepository fileRepository) {
            List<TempGameOption> options = tempGameOptions.stream()
                    .map(option -> option.toEntity(fileRepository))
                    .toList();

            return TempGame.builder()
                    .description(description)
                    .tempGameOptions(options)
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "임시 밸런스 게임 응답")
    public static class TempGameResponse {

        @Schema(description = "게임 추가 설명", example = "추가 설명")
        private String description;

        private List<TempGameOptionDto> tempGameOptions;

        public static TempGameResponse fromEntity(TempGame tempGame, FileRepository fileRepository) {
            List<TempGameOptionDto> tempGameOptions = tempGame.getTempGameOptions().stream()
                    .map(option -> {
                        Long fileId = fileRepository.findIdByResourceIdAndFileType(
                                option.getId(), FileType.TEMP_GAME_OPTION);
                        return TempGameOptionDto.fromEntity(option, fileId);
                    })
                    .toList();

            return TempGameResponse.builder()
                    .description(tempGame.getDescription())
                    .tempGameOptions(tempGameOptions)
                    .build();
        }
    }
}
