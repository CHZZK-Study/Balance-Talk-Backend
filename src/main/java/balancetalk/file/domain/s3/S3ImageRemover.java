package balancetalk.file.domain.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Component
@RequiredArgsConstructor
public class S3ImageRemover {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public void removeImageFromBucket(String key) {
        s3Client.deleteObject(getDeleteObjectRequest(key));
    }

    private DeleteObjectRequest getDeleteObjectRequest(String key) {
        return DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
    }
}
