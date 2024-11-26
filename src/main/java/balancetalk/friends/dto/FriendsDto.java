package balancetalk.friends.dto;

import balancetalk.file.domain.File;
import balancetalk.friends.domain.Friends;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class FriendsDto {

    @Schema(description = "PICK-O 프렌즈 캐릭터 생성 요청")
    @Data
    @AllArgsConstructor
    public static class CreateFriendsRequest {

        @Schema(description = "프렌즈 이름", example = "꺼북이")
        @NotBlank(message = "프렌즈 이름은 필수입니다.")
        private String name;

        @Schema(description = "첨부한 이미지 파일 ID", example = "41")
        @NotNull(message = "프렌즈 이미지 ID는 필수입니다.")
        private Long imgId;

        public Friends toEntity() {
            return Friends.builder()
                    .name(name)
                    .imgId(imgId)
                    .build();
        }
    }

    @Schema(description = "PICK-O 프렌즈 캐릭터 생성 요청")
    @Data
    @Builder
    @AllArgsConstructor
    public static class FriendsImageResponse {

        @Schema(description = "프렌즈 이미지 파일 ID", example = "3")
        private Long fileId;

        @Schema(description = "프렌즈 이미지 URL",
                example = "https://picko-image.amazonaws.com/friends/ad80-a94e08-3301d2_대해파리.png")
        private String imgUrl;

        public static FriendsImageResponse from(File file) {
            return FriendsImageResponse.builder()
                    .fileId(file.getId())
                    .imgUrl(file.getImgUrl())
                    .build();
        }
    }
}
