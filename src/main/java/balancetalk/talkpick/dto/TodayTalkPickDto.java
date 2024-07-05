package balancetalk.talkpick.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

public class TodayTalkPickDto {

    @Schema(description = "오늘의 톡픽 조회 (메인 페이지) 응답")
    @Data
    public static class TodayTalkPickResponse {

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

        @QueryProjection
        public TodayTalkPickResponse(Long id, String title, String summary, String optionA, String optionB) {
            this.id = id;
            this.title = title;
            this.summary = summary;
            this.optionA = optionA;
            this.optionB = optionB;
        }
    }
}
