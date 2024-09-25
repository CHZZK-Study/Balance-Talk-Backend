package balancetalk.game.dto;

import balancetalk.game.domain.TempGame;
import balancetalk.game.dto.TempGameOptionDto.CreateTempGameOption;
import balancetalk.game.dto.TempGameOptionDto.FindTempGameOption;
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

        @Schema(description = "제목", example = "제목")
        private String title;

        @Schema(description = "게임 추가 설명", example = "추가 설명")
        private String description;

        private List<CreateTempGameOption> tempGameOptions;

        public TempGame toEntity() {
            return TempGame.builder()
                    .title(title)
                    .description(description)
                    .tempGameOptions(tempGameOptions.stream().map(CreateTempGameOption::toEntity).toList())
                    .bookmarks(0L)
                    .build();
        }
    }

    @Data
    @Builder
    @Schema(description = "임시 밸런스 게임 조회 응답")
    public static class TempGameResponse {
        @Schema(description = "밸런스 게임 id", example = "1")
        private Long id;

        @Schema(description = "밸런스 게임 제목", example = "제목")
        private String title;

        @Schema(description = "게임 추가 설명", example = "추가 설명")
        private String description;

        private List<FindTempGameOption> tempGameOptions;

        public static TempGameResponse fromEntity(TempGame tempGame) {
            return TempGameResponse.builder()
                    .id(tempGame.getId())
                    .title(tempGame.getTitle())
                    .description(tempGame.getDescription())
                    .tempGameOptions(tempGame.getTempGameOptions().stream().map(FindTempGameOption::fromEntity).toList())
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "임시 밸런스 게임 상세 조회 응답")
    public static class TempGameDetailResponse {

        @Schema(description = "밸런스 게임 제목", example = "제목")
        private String title;

        @Schema(description = "게임 추가 설명", example = "추가 설명")
        private String description;

        private List<FindTempGameOption> tempGameOptions;

        public static TempGameDetailResponse fromEntity(TempGame tempGame) {
            return TempGameDetailResponse.builder()
                    .title(tempGame.getTitle())
                    .description(tempGame.getDescription())
                    .tempGameOptions(tempGame.getTempGameOptions().stream().map(FindTempGameOption::fromEntity).toList())
                    .build();
        }
    }

}
