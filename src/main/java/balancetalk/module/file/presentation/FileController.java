package balancetalk.module.file.presentation;

import balancetalk.module.file.application.FileService;
import balancetalk.module.file.application.S3UploadService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {
    private final FileService fileService;
    private final S3UploadService s3UploadService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        return s3UploadService.uploadImage(file);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/upload/multiple")
    public List<String> uploadMultipleImages(@RequestParam("file") List<MultipartFile> files) {
        return s3UploadService.uploadMultipleImage(files);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/download/{fileId}")
    public String downloadFile(@PathVariable Long fileId) {
        fileService.downloadFile(fileId);
        return "파일이 다운로드되었습니다.";
    }
}