package balancetalk.talkpick.dto;

import balancetalk.member.domain.Member;
import balancetalk.talkpick.domain.TempTalkPick;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class TempTalkPickDto {

    @Schema(description = "톡픽 임시 저장 요청")
    @Data
    @AllArgsConstructor
    public static class SaveTempTalkPickRequest {

        private BaseTalkPickFields baseFields;

        @Schema(description = "첨부한 이미지 ID 목록", example = "[214, 24]")
        private List<Long> fileIds;

        public TempTalkPick toEntity(Member member) {
            return TempTalkPick.builder()
                    .title(baseFields.getTitle())
                    .content(baseFields.getContent())
                    .optionA(baseFields.getOptionA())
                    .optionB(baseFields.getOptionB())
                    .sourceUrl(baseFields.getSourceUrl())
                    .member(member)
                    .build();
        }
    }

    @Schema(description = "임시 저장한 톡픽 조회 응답")
    @Data
    @Builder
    @AllArgsConstructor
    public static class FindTempTalkPickResponse {

        private BaseTalkPickFields baseFields;

        @Schema(description = "톡픽 작성 시 첨부한 이미지 URL 목록",
                example = "["
                        + "\"https://picko-image.amazonaws.com/temp-talks/4e54-ad80-a94e083301d2_czz.png\",\n"
                        + "\"https://picko-image.amazonaws.com/temp-talks/d5d4-4e47-b5d0-957e6ed4830b_prom.jpeg\""
                        + "]")
        private List<String> imgUrls;

        @Schema(description = "첨부한 이미지 ID 목록", example = "[214, 24]")
        private List<Long> fileIds;

        public static FindTempTalkPickResponse from(TempTalkPick entity, List<String> imgUrls, List<Long> fileIds) {
            return FindTempTalkPickResponse.builder()
                    .baseFields(BaseTalkPickFields.from(
                            entity.getTitle(),
                            entity.getContent(),
                            entity.getOptionA(),
                            entity.getOptionB(),
                            entity.getSourceUrl()))
                    .imgUrls(imgUrls)
                    .fileIds(fileIds)
                    .build();
        }
    }
}
