package balancetalk.file.domain.s3;

import balancetalk.global.exception.BalanceTalkException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;

import static balancetalk.global.exception.ErrorCode.FILE_UPLOAD_FAILED;

@Component
@RequiredArgsConstructor
public class S3ImageUploader {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public void uploadImageToBucket(MultipartFile multipartFile, String uploadDir, String storedName) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            putObject(uploadDir + storedName, inputStream, multipartFile.getSize());
        } catch (IOException e) {
            throw new BalanceTalkException(FILE_UPLOAD_FAILED);
        }
    }

    private void putObject(String key, InputStream inputStream, long size) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, size));
    }
}
