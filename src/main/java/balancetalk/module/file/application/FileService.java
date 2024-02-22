package balancetalk.module.file.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.module.file.domain.File;
import balancetalk.module.file.domain.FileRepository;
import balancetalk.module.file.domain.FileType;
import balancetalk.module.file.dto.FileDto;
import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

import static balancetalk.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final ServletContext servletContext;

    // 파일 업로드
    @Transactional
    public File uploadFile(MultipartFile file) {
        String uploadDir = "C:\\Users\\King\\Desktop"; // TODO : 경로 configuration 파일로 빼기
        String originalFileName = file.getOriginalFilename();
        String storedFileName = UUID.randomUUID().toString().replace("-", "") + "_" + originalFileName;
        String path = Paths.get(uploadDir, storedFileName).toString();
        String contentType = file.getContentType();
        FileType fileType = convertMimeTypeToFileType(contentType);

        FileDto fileDto = FileDto.of(file, storedFileName, path, fileType);
        File saveFile = fileDto.toEntity();

        try {
            Files.copy(file.getInputStream(), Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BalanceTalkException(FILE_UPLOAD_FAILED);
        }
        return fileRepository.save(saveFile);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Resource> downloadFile(Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_DIRECTORY));
        Path path = Paths.get(file.getPath());

        try {
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new BalanceTalkException(NOT_FOUND_FILE);
            }

            String contentType = servletContext.getMimeType(resource.getFile().getAbsolutePath());

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getUploadName() + "\"")
                    .body(resource);

        } catch (IOException e) {
            throw new BalanceTalkException(FILE_DOWNLOAD_FAILED);
        }
    }

    private FileType convertMimeTypeToFileType(String mimeType) {
        if (mimeType == null) {
            throw new BalanceTalkException(MIME_TYPE_NULL);
        }

        return Arrays.stream(FileType.values())
                .filter(type -> type.getMimeType().equalsIgnoreCase(mimeType))
                .findFirst()
                .orElseThrow(() -> new BalanceTalkException(NOT_SUPPORTED_FILE_TYPE));
    }
}
