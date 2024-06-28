package balancetalk.module.post.dto;

import balancetalk.module.vote.domain.Option;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class TalkResponse {

    @Schema(description = "게시글 id", example = "1")
    private Long id;

    @Schema(description = "제목", example = "깻잎 논쟁 중 당신의 선택은?")
    private String title;

    @Schema(description = "내용", example = "오늘의 톡픽 내용")
    private String content;

    @Schema(description = "게시글 요약", example = "1. ~~~, 2. ~~~, 3.~~~")
    private String summary;

    @Schema(description = "게시글 조회수", example = "126")
    private long views;

    @Schema(description = "게시글 추천수", example = "15")
    private long likesCount;

    @Schema(description = "북마크 여부", example = "false")
    private boolean myBookmark;

    @Schema(description = "추천 여부", example = "true")
    private boolean myLike;

    @Schema(description = "투표 여부", example = "true")
    private boolean myVote;

    @Schema(description = "투표한 선택지 옵션", example = "A")
    private Option option;

    private List<String> imageUrls;
}
