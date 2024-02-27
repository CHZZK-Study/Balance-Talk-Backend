package balancetalk.module.file.presentation;

import balancetalk.module.file.application.FileService;
import balancetalk.module.file.application.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {
    private final FileService fileService;
    private final S3UploadService s3UploadService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/image/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        return s3UploadService.uploadImage(file);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/download/{fileId}")
    public String downloadFile(@PathVariable Long fileId) {
        fileService.downloadFile(fileId);
        return "파일이 다운로드되었습니다.";
    }
}