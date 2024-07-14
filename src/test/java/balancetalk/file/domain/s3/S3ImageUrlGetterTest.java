package balancetalk.file.domain.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ImageUrlGetterTest {

    @Mock
    S3Client s3Client;

    @Mock
    S3Utilities s3Utilities;

    S3ImageUrlGetter s3ImageUrlGetter;

    @BeforeEach
    void setUp() {
        s3ImageUrlGetter = new S3ImageUrlGetter();
    }

    @Test
    @DisplayName("S3에 업로드된 이미지 URL을 성공적으로 가져온다.")
    void getImageUrl_Success() throws MalformedURLException {
        // given
        URL url = new URL("https://image-url.com");

        when(s3Client.utilities()).thenReturn(s3Utilities);
        when(s3Client.utilities().getUrl(any(GetUrlRequest.class))).thenReturn(url);

        // when
        String result = s3ImageUrlGetter.getImageUrl(s3Client, "test-bucket", "test-key");

        // then
        assertThat(result).isEqualTo("https://image-url.com");
    }
}