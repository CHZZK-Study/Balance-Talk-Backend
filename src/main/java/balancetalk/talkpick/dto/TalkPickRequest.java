package balancetalk.talkpick.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TalkPickRequest {

    @Schema(description = "제목", example = "제목")
    private String title;

    @Schema(description = "본문 내용", example = "본문 내용")
    private String content;

    @Schema(description = "요약", example = "3줄 요약")
    private String summary;

    @Schema(description = "선택지 A 이름", example = "선택지 A 이름")
    private String optionA; // TODO "O"로 자동 지정

    @Schema(description = "선택지 B 이름", example = "선택지 B 이름")
    private String optionB; // TODO "X"로 자동 지정
}
