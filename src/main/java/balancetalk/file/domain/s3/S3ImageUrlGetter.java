package balancetalk.file.domain.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;

@Component
@RequiredArgsConstructor
public class S3ImageUrlGetter {

    public String getUrl(S3Client s3Client, String bucket, String key) {
        GetUrlRequest request = GetUrlRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        return s3Client.utilities().getUrl(request).toString();
    }
}
