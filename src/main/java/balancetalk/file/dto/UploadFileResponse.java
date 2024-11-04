package balancetalk.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "이미지 파일 업로드 응답")
@Data
@AllArgsConstructor
public class UploadFileResponse {

    @Schema(description = "업로드한 이미지 URL 목록",
            example = "["
                    + "\"https://picko-image.s3.ap-northeast-2.amazonaws.com/talk-pick/ad80-a94e083301d2_czz.png\",\n"
                    + "\"https://picko-image.s3.ap-northeast-2.amazonaws.com/talk-pick/957e6ed4830b_prom.jpeg\""
                    + "]")
    private List<String> imgUrls;

    @Schema(description = "업로드한 이미지 파일 ID 목록", example = "[121, 255]")
    private List<Long> fileIds;
}
