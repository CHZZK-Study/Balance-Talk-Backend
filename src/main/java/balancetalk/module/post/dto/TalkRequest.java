package balancetalk.module.post.dto;

import balancetalk.module.file.domain.File;
import balancetalk.module.vote.Option;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class TalkRequest {

    @Schema(description = "제목", example = "깻잎 논쟁 중 당신의 선택은?")
    private String title;

    @Schema(description = "내용", example = "오늘의 톡픽 내용")
    private String content;

    @Schema(description = "게시글 요약", example = "1. ~~~, 2. ~~~, 3.~~~")
    private String summary;

    private List<Option> options;

    private List<String> storedImageNames;
}
