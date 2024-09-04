package balancetalk.talkpick.dto;

import balancetalk.talkpick.domain.TalkPick;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "톡픽 검색 응답")
@Data
public class SearchTalkPickResponse {

    @Schema(description = "제목", example = "톡픽 제목")
    private String title;

    private SummaryResponse summary;

    @Schema(description = "본문 내용", example = "톡픽 본문 내용")
    private String content;

    @Schema(description = "선택지 A 이름", example = "선택지 A 이름")
    private String optionA;

    @Schema(description = "선택지 B 이름", example = "선택지 B 이름")
    private String optionB;

    @Schema(description = "첫 번째 이미지 URL", example = "https://picko-image.s3.ap-northeast-2.amazonaws.com/temp-talk-pick/9b4856fe-b624-4e54-ad80-a94e083301d2_czz.png")
    private String firstImgUrl;

    public SearchTalkPickResponse(TalkPick talkPick) {
        this.title = talkPick.getTitle();
        this.summary = new SummaryResponse(talkPick.getSummary());
        this.content = talkPick.getContent();
        this.optionA = talkPick.getOptionA();
        this.optionB = talkPick.getOptionB();
    }
}
