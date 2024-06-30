package balancetalk.game.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GameDetailResponse {
    @Schema(description = "밸런스 게임 id", example = "1")
    private Long id;

    @Schema(description = "밸런스 게임 제목", example = "제목")
    private String title;

    @Schema(description = "선택지 A 이름", example = "선택지 A 이름")
    private String optionA;

    @Schema(description = "선택지 B 이름", example = "선택지 B 이름")
    private String optionB;

    @Schema(description = "북마크 여부", example = "false")
    private Boolean myBookmark;

    @Schema(description = "투표 여부", example = "false")
    private Boolean myVote;
}

