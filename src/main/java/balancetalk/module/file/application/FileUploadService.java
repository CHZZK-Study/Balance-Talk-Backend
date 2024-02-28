package balancetalk.module.file.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.module.file.domain.File;
import balancetalk.module.file.domain.FileRepository;
import balancetalk.module.file.domain.FileType;
import balancetalk.module.file.dto.FileDto;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static balancetalk.global.exception.ErrorCode.MIME_TYPE_NULL;
import static balancetalk.global.exception.ErrorCode.NOT_SUPPORTED_FILE_TYPE;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final S3Client s3Client;
    private final FileRepository fileRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public FileDto uploadImage(MultipartFile multipartFile) {
        String uploadDir = "balance-talk-images/balance-option/";
        String originalName = multipartFile.getOriginalFilename();
        String storedName = String.format("%s_%s", UUID.randomUUID(), originalName);
        long contentLength = multipartFile.getSize();
        FileType fileType = convertMimeTypeToFileType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            putObjectToS3(uploadDir + storedName, inputStream, contentLength);
            File file = fileRepository.save(createFile(originalName, storedName, uploadDir, fileType, contentLength));

            return FileDto.fromEntity(file);
        } catch (IOException e) {
            throw new BalanceTalkException(ErrorCode.FILE_UPLOAD_FAILED);
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

    private void putObjectToS3(String key, InputStream inputStream, long contentLength) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
    }

    private File createFile(String originalName, String storedName, String uploadDir, FileType fileType,
                            long contentLength) {

        return File.builder()
                .originalName(originalName)
                .storedName(storedName)
                .path(uploadDir)
                .type(fileType)
                .size(contentLength)
                .build();
    }
}
