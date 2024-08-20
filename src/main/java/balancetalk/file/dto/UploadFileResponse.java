package balancetalk.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Schema(description = "이미지 파일 업로드 응답")
@Data
@AllArgsConstructor
public class UploadFileResponse {

    @Schema(description = "업로드한 이미지 URL 목록",
            example = "[" +
                    "\"https://picko-image.s3.ap-northeast-2.amazonaws.com/talk-pick/9b4856fe-b624-4e54-ad80-a94e083301d2_czz.png\",\n" +
                    "\"https://picko-image.s3.ap-northeast-2.amazonaws.com/talk-pick/fdcbd97b-f9be-45d1-b855-43f3fd17d5a6_6d588490-d5d4-4e47-b5d0-957e6ed4830b_prom.jpeg\"" +
                    "]")
    private List<String> imgUrls;

    @Schema(description = "업로드한 이미지 고유 이름 목록",
            example = "[" +
                    "\"9b4856fe-b624-4e54-ad80-a94e083301d2_czz.png\",\n" +
                    "\"fdcbd97b-f9be-45d1-b855-43f3fd17d5a6_6d588490-d5d4-4e47-b5d0-957e6ed4830b_prom.jpeg\"" +
                    "]")
    private List<String> storedNames;
}
