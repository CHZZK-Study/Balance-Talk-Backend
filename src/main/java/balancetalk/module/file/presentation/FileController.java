package balancetalk.module.file.presentation;

import balancetalk.module.file.application.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {
    private final FileService fileService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        fileService.uploadFile(file);
        return "파일이 업로드되었습니다.";
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/download/{fileId}")
    public String downloadFile(@PathVariable Long fileId) {
        fileService.downloadFile(fileId);
        return "파일이 다운로드되었습니다.";
    }
}