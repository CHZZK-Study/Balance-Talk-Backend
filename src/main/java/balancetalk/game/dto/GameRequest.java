package balancetalk.game.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GameRequest {

    @Schema(description = "제목", example = "제목")
    private String title;

    @Schema(description = "선택지 A 이름", example = "선택지 A 이름")
    private String optionA;

    @Schema(description = "선택지 B 이름", example = "선택지 B 이름")
    private String optionB;
}
