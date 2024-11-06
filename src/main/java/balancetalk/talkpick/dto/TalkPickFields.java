package balancetalk.talkpick.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TalkPickFields {

    @Schema(description = "제목", example = "제목")
    @NotBlank(message = "제목은 공백을 허용하지 않습니다.")
    @Size(max = 50, message = "제목은 50자 이하여야 합니다.")
    private String title;

    @Schema(description = "본문 내용", example = "본문 내용")
    @NotBlank(message = "본문 내용은 공백을 허용하지 않습니다.")
    private String content;

    @Schema(description = "선택지 A 이름", example = "선택지 A 이름")
    @NotBlank(message = "선택지 이름은 공백을 허용하지 않습니다.")
    @Size(max = 10, message = "선택지 이름은 10자 이하여야 합니다.")
    private String optionA;

    @Schema(description = "선택지 B 이름", example = "선택지 B 이름")
    @NotBlank(message = "선택지 이름은 공백을 허용하지 않습니다.")
    @Size(max = 10, message = "선택지 이름은 10자 이하여야 합니다.")
    private String optionB;

    @Schema(description = "출처 URL", example = "https://github.com/CHZZK-Study/Balance-Talk-Backend/issues/506")
    private String sourceUrl;
}
