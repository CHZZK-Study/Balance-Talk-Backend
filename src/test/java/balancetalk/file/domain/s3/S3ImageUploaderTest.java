package balancetalk.file.domain.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ImageUploaderTest {

    @Mock
    S3Client s3Client;

    @Mock
    MultipartFile multipartFile;

    S3ImageUploader s3ImageUploader;

    @BeforeEach
    void setUp() {
        s3ImageUploader = new S3ImageUploader();
    }

    @Test
    @DisplayName("S3에 이미지를 성공적으로 업로드한다.")
    void uploadImageToBucket_Success() throws IOException {
        // given
        String bucket = "test-bucket";
        String key = "test-key";
        byte[] content = "test-content".getBytes();
        InputStream inputStream = new ByteArrayInputStream(content);

        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(multipartFile.getSize()).thenReturn((long) content.length);

        // when
        s3ImageUploader.uploadImageToBucket(s3Client, bucket, key, multipartFile);

        // then
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
}