package balancetalk.talkpick.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TodayTalkPickResponse {

    @Schema(description = "톡픽 ID", example = "톡픽 ID")
    private Long id;

    @Schema(description = "제목", example = "톡픽 제목")
    private String title;

    @Schema(description = "요약", example = "3줄 요약")
    private String summary;

    @Schema(description = "선택지 A 이름", example = "선택지 A 이름")
    private String optionA; // TODO "O"로 자동 지정

    @Schema(description = "선택지 B 이름", example = "선택지 B 이름")
    private String optionB; // TODO "X"로 자동 지정
}
