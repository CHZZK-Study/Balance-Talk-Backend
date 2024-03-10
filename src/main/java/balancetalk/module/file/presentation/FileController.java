package balancetalk.module.file.presentation;

import balancetalk.module.file.application.FileUploadService;
import balancetalk.module.file.dto.FileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
@Tag(name = "file", description = "파일 API")
public class FileController {

    private final FileUploadService fileUploadService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "파일 업로드", description = "파일을 업로드 한다.")
    public FileResponse uploadImage(@RequestParam("file") MultipartFile file) {
        return fileUploadService.uploadImage(file);
    }
}