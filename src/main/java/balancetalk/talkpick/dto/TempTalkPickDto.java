package balancetalk.talkpick.dto;

import balancetalk.member.domain.Member;
import balancetalk.talkpick.domain.TempTalkPick;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class TempTalkPickDto {

    @Schema(description = "톡픽 임시 저장 요청")
    @Data
    @AllArgsConstructor
    public static class SaveTempTalkPickRequest {

        @Schema(description = "제목", example = "제목")
        @NotBlank
        @Size(max = 50)
        private String title;

        @Schema(description = "본문 내용", example = "본문 내용")
        @NotBlank
        private String content;

        @Schema(description = "선택지 A 이름", example = "선택지 A 이름")
        @NotBlank
        @Size(max = 10)
        private String optionA;

        @Schema(description = "선택지 B 이름", example = "선택지 B 이름")
        @NotBlank
        @Size(max = 10)
        private String optionB;

        public TempTalkPick toEntity(Member member) {
            return TempTalkPick.builder()
                    .title(title)
                    .content(content)
                    .optionA(optionA)
                    .optionB(optionB)
                    .member(member)
                    .build();
        }
    }

    @Schema(description = "임시 저장한 톡픽 조회 응답")
    @Data
    @Builder
    @AllArgsConstructor
    public static class FindTempTalkPickResponse {

        @Schema(description = "제목", example = "제목")
        private String title;

        @Schema(description = "본문 내용", example = "본문 내용")
        private String content;

        @Schema(description = "선택지 A 이름", example = "선택지 A 이름")
        private String optionA;

        @Schema(description = "선택지 B 이름", example = "선택지 B 이름")
        private String optionB;

        public static FindTempTalkPickResponse from(TempTalkPick entity) {
            return FindTempTalkPickResponse.builder()
                    .title(entity.getTitle())
                    .content(entity.getContent())
                    .optionA(entity.getOptionA())
                    .optionB(entity.getOptionB())
                    .build();
        }
    }
}
