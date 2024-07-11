package balancetalk.file.presentation;

import balancetalk.file.application.FileService;
import balancetalk.file.domain.FileType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
@Tag(name = "image", description = "이미지 파일 API")
public class FileController {

    private final FileService fileService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "이미지 파일 업로드", description = "이미지 파일을 업로드 한다.")
    public String uploadImage(@RequestPart("file") MultipartFile file,
                              @RequestParam Long resourceId,
                              @RequestParam("type") FileType fileType) {
        return fileService.uploadImage(file, resourceId, fileType);
    }
}