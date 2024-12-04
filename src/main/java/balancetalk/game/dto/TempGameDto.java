package balancetalk.game.dto;

import balancetalk.file.domain.File;
import balancetalk.file.domain.FileType;
import balancetalk.file.domain.repository.FileRepository;
import balancetalk.game.domain.TempGame;
import balancetalk.game.domain.TempGameOption;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
            List<Long> resourceIds = tempGame.getTempGameOptions().stream()
                    .filter(option -> option.getImgId() != null)
                    .map(TempGameOption::getId)
                    .toList();

            List<File> files = fileRepository.findAllByResourceIdsAndFileType(resourceIds,
                    FileType.TEMP_GAME_OPTION);

            Map<Long, File> fileMap = files.stream()
                    .collect(Collectors.toMap(File::getResourceId, file -> file));

            List<TempGameOptionDto> tempGameOptions = tempGame.getTempGameOptions().stream()
                    .map(option -> {
                        File file = fileMap.get(option.getId());
                        if (file == null) {
                            return TempGameOptionDto.fromEntity(option, null, null);
                        }
                        return TempGameOptionDto.fromEntity(option, file.getId(), file.getImgUrl());
                    })
                    .toList();

            return TempGameResponse.builder()
                    .description(tempGame.getDescription())
                    .tempGameOptions(tempGameOptions)
                    .build();
        }
    }
}
