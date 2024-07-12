package balancetalk.file.domain.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Component
@RequiredArgsConstructor
public class S3ImageRemover {

    public void removeImageFromBucket(S3Client s3Client, String bucket, String key) {
        s3Client.deleteObject(getDeleteObjectRequest(bucket, key));
    }

    private DeleteObjectRequest getDeleteObjectRequest(String bucket, String key) {
        return DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
    }
}
