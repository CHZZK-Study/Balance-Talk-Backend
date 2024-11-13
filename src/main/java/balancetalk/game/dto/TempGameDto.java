package balancetalk.game.dto;

import balancetalk.file.domain.repository.FileRepository;
import balancetalk.game.domain.GameOption;
import balancetalk.game.domain.TempGame;
import balancetalk.game.domain.TempGameOption;
import balancetalk.game.dto.TempGameOptionDto.CreateTempGameOption;
import balancetalk.game.dto.TempGameOptionDto.FindTempGameOption;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
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

        private List<CreateTempGameOption> tempGameOptions;

        public TempGame toEntity(FileRepository fileRepository) {
            List<TempGameOption> options = tempGameOptions.stream()
                    .map(option -> option.toEntity(fileRepository))
                    .toList();

            return TempGame.builder()
                    .description(description)
                    .tempGameOptions(options)
                    .editedAt(LocalDateTime.now())
                    .build();
        }

        public List<Long> getFileIds() {
            return this.tempGameOptions.stream()
                    .map(CreateTempGameOption::getFileId)
                    .toList();
        }
    }

//    @Data
//    @Builder
//    @AllArgsConstructor
//    @Schema(description = "임시 밸런스 게임 상세 조회 응답")
//    public static class TempGameDetailResponse {
//
//
//
//        @Schema(description = "게임 추가 설명", example = "추가 설명")
//        private String description;
//
//        private List<FindTempGameOption> tempGameOptions;
//
//        public static TempGameDetailResponse fromEntity(TempGame tempGame) {
//            return TempGameDetailResponse.builder()
//                    .title(tempGame.getTitle())
//                    .description(tempGame.getDescription())
//                    .tempGameOptions(tempGame.getTempGameOptions().stream().map(FindTempGameOption::fromEntity).toList())
//                    .build();
//        }
//    }

}
