package balancetalk.module.file.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.module.file.domain.File;
import balancetalk.module.file.domain.FileRepository;
import balancetalk.module.file.domain.FileType;
import balancetalk.module.file.dto.FileResponse;
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
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class FileService {

    private static final String S3_URL = "https://balance-talk-static-files.s3.ap-northeast-2.amazonaws.com/";
    private static final String UPLOAD_DIR = "balance-talk-images/balance-option/";

    private final S3Client s3Client;
    private final FileRepository fileRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public FileResponse uploadImage(MultipartFile multipartFile) {
        String originalName = multipartFile.getOriginalFilename();
        String storedName = String.format("%s_%s", UUID.randomUUID(), originalName);
        long contentLength = multipartFile.getSize();
        FileType fileType = convertMimeTypeToFileType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            putObjectToS3(UPLOAD_DIR + storedName, inputStream, contentLength);
            File file = fileRepository.save(
                    createFile(originalName, storedName, S3_URL + UPLOAD_DIR, fileType, contentLength));

            return FileResponse.fromEntity(file);
        } catch (IOException e) {
            throw new BalanceTalkException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    private FileType convertMimeTypeToFileType(String mimeType) {
        if (mimeType == null) {
            throw new IllegalArgumentException("MIME 타입은 NULL이 될 수 없습니다.");
        }

        return Arrays.stream(FileType.values())
                .filter(type -> type.getMimeType().equalsIgnoreCase(mimeType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 파일 타입 : " + mimeType));
    }

    private void putObjectToS3(String key, InputStream inputStream, long contentLength) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
    }

    private File createFile(String originalName, String storedName, String path, FileType fileType,
                            long contentLength) {

        return File.builder()
                .originalName(originalName)
                .storedName(storedName)
                .path(path)
                .type(fileType)
                .size(contentLength)
                .build();
    }

    @Transactional(readOnly = true)
    public String getUrl(String key) {
        GetUrlRequest request = GetUrlRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        return s3Client.utilities().getUrl(request).toString();
    }
}
