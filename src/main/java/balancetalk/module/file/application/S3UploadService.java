package balancetalk.module.file.application;

import balancetalk.module.file.domain.File;
import balancetalk.module.file.domain.FileRepository;
import balancetalk.module.file.domain.FileType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final S3Client s3Client;
    private final FileRepository fileRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public List<String> uploadMultipleImage(List<MultipartFile> multipartFiles) {
        return multipartFiles.stream()
                .map(this::uploadImage)
                .collect(Collectors.toList());
    }

    @Transactional
    public String uploadImage(MultipartFile multipartFile) {
        String uploadDir = "balance-talk-images/balance-option/";
        String originalName = multipartFile.getOriginalFilename();
        String storedName = String.format("%s_%s", UUID.randomUUID(), originalName);
        long contentLength = multipartFile.getSize();
        FileType fileType = convertMimeTypeToFileType(multipartFile.getContentType());
        try (InputStream inputStream = multipartFile.getInputStream()) {

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(uploadDir + storedName)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));

            File file = File.builder()
                    .originalName(originalName)
                    .storedName(storedName)
                    .path(uploadDir)
                    .type(fileType)
                    .size(contentLength)
                    .build();
            File saved = fileRepository.save(file);

            return saved.getPath() + saved.getStoredName();
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO 예외 처리
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
}
