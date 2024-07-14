package balancetalk.file.presentation;

import balancetalk.file.application.FileService;
import balancetalk.file.domain.FileType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
@Tag(name = "file", description = "파일 API")
public class FileController {

    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "이미지 파일 업로드", description = "이미지 파일을 업로드한 후 이미지 URL을 반환 받습니다.")
    public String uploadImage(@RequestPart("file") MultipartFile file,
                              @Parameter(description = "타겟 리소스의 ID") @RequestParam Long resourceId,
                              @Parameter(description = "타겟 리소스의 타입") @RequestParam("type") FileType fileType) {
        return fileService.uploadImage(file, resourceId, fileType);
    }
}
