package balancetalk.talkpick.dto;

import balancetalk.member.domain.Member;
import balancetalk.talkpick.domain.TempTalkPick;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

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

        @Schema(description = "출처 URL", example = "https://github.com/CHZZK-Study/Balance-Talk-Backend/issues/506")
        private String sourceUrl;

        @Schema(description = "첨부한 이미지 고유 이름 목록",
                example = "[" +
                        "\"9b4856fe-b624-4e54-ad80-a94e083301d2_czz.png\",\n" +
                        "\"fdcbd97b-f9be-45d1-b855-43f3fd17d5a6_6d588490-d5d4-4e47-b5d0-957e6ed4830b_prom.jpeg\"" +
                        "]")
        private List<String> storedNames;

        public TempTalkPick toEntity(Member member) {
            return TempTalkPick.builder()
                    .title(title)
                    .content(content)
                    .optionA(optionA)
                    .optionB(optionB)
                    .sourceUrl(sourceUrl)
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

        @Schema(description = "출처 URL", example = "https://github.com/CHZZK-Study/Balance-Talk-Backend/issues/506")
        private String sourceUrl;

        @Schema(description = "톡픽 작성 시 첨부한 이미지 URL 목록",
                example = "[" +
                        "\"https://picko-image.s3.ap-northeast-2.amazonaws.com/temp-talk-pick/9b4856fe-b624-4e54-ad80-a94e083301d2_czz.png\",\n" +
                        "\"https://picko-image.s3.ap-northeast-2.amazonaws.com/temp-talk-pick/fdcbd97b-f9be-45d1-b855-43f3fd17d5a6_6d588490-d5d4-4e47-b5d0-957e6ed4830b_prom.jpeg\"" +
                        "]")
        private List<String> imgUrls;

        @Schema(description = "첨부한 이미지 고유 이름 목록",
                example = "[" +
                        "\"9b4856fe-b624-4e54-ad80-a94e083301d2_czz.png\",\n" +
                        "\"fdcbd97b-f9be-45d1-b855-43f3fd17d5a6_6d588490-d5d4-4e47-b5d0-957e6ed4830b_prom.jpeg\"" +
                        "]")
        private List<String> storedNames;

        public static FindTempTalkPickResponse from(TempTalkPick entity, List<String> imgUrls, List<String> storedNames) {
            return FindTempTalkPickResponse.builder()
                    .title(entity.getTitle())
                    .content(entity.getContent())
                    .optionA(entity.getOptionA())
                    .optionB(entity.getOptionB())
                    .sourceUrl(entity.getSourceUrl())
                    .imgUrls(imgUrls)
                    .storedNames(storedNames)
                    .build();
        }
    }
}
