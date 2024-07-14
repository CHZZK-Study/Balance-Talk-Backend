package balancetalk.file.domain.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ImageRemoverTest {

    @Mock
    S3Client s3Client;

    S3ImageRemover s3ImageRemover;

    @BeforeEach
    void setUp() {
        s3ImageRemover = new S3ImageRemover();
    }

    @Test
    @DisplayName("S3에 업로드된 이미지를 성공적으로 제거한다.")
    void removeImageFromBucket_Success() {
        // given
        String bucket = "test-bucket";
        String key = "test-key";

        // when
        s3ImageRemover.removeImageFromBucket(s3Client, bucket, key);

        // then
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }
}