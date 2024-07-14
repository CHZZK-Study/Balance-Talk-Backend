package balancetalk.file.domain.s3;

import balancetalk.global.exception.BalanceTalkException;
import lombok.RequiredArgsConstructor;
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

    public void uploadImageToBucket(S3Client s3Client, String bucket, String key, MultipartFile multipartFile) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            s3Client.putObject(getPutObjectRequest(bucket, key), getRequestBody(multipartFile, inputStream));
        } catch (IOException e) {
            throw new BalanceTalkException(FILE_UPLOAD_FAILED);
        }
    }

    private PutObjectRequest getPutObjectRequest(String bucket, String key) {
        return PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
    }

    private RequestBody getRequestBody(MultipartFile multipartFile, InputStream inputStream) {
        return RequestBody.fromInputStream(inputStream, multipartFile.getSize());
    }
}
