package balancetalk.file.presentation;

import balancetalk.file.application.FileService;
import balancetalk.file.domain.FileType;
import balancetalk.file.domain.MultipartFiles;
import balancetalk.file.dto.UploadFileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
@Tag(name = "file", description = "파일 API")
public class FileController {

    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "이미지 파일 업로드", description = "이미지 파일을 업로드한 후 이미지 URL을 반환 받습니다.")
    public UploadFileResponse deleteImage(@RequestPart("file") List<MultipartFile> multipartFiles,
                                          @Parameter(description = "리소스 타입", example = "TALK_PICK")
                                          @RequestParam("type") FileType fileType) {
        return fileService.uploadImages(new MultipartFiles(multipartFiles, fileType));
    }

    @DeleteMapping("/{fileId}")
    @Operation(summary = "이미지 파일 제거", description = "첨부한 이미지 파일을 제거합니다.")
    public void deleteImage(@PathVariable Long fileId) {
        fileService.deleteImageById(fileId);
    }
}
