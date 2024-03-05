package balancetalk.module.file.presentation;

import balancetalk.module.file.application.FileUploadService;
import balancetalk.module.file.dto.FileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final FileUploadService fileUploadService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/image/upload")
    public FileResponse uploadImage(@RequestParam("file") MultipartFile file) {
        return fileUploadService.uploadImage(file);
    }
}