package balancetalk.friends.dto;

import balancetalk.friends.domain.Friends;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
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
}
