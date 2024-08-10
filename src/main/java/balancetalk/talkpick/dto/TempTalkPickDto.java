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
    public static class Request {

        @NotBlank
        @Size(max = 50)
        private String title;

        @NotBlank
        private String content;

        @NotBlank
        @Size(max = 10)
        private String optionA;

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

    @Schema(description = "톡픽 임시 저장 요청")
    @Data
    @Builder
    @AllArgsConstructor
    public static class Response {

        private String title;
        private String content;
        private String optionA;
        private String optionB;

        public static Response from(TempTalkPick entity) {
            return Response.builder()
                    .title(entity.getTitle())
                    .content(entity.getContent())
                    .optionA(entity.getOptionA())
                    .optionB(entity.getOptionB())
                    .build();
        }
    }
}
