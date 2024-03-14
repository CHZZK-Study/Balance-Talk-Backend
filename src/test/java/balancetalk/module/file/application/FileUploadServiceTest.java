package balancetalk.module.file.application;

import balancetalk.module.file.domain.File;
import balancetalk.module.file.domain.FileRepository;
import balancetalk.module.file.domain.FileType;
import balancetalk.module.file.dto.FileDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileUploadServiceTest {

    @InjectMocks
    private FileUploadService fileUploadService;

    @Mock
    private S3Client s3Client;

    @Mock
    private FileRepository fileRepository;

    @BeforeEach
    void setUp() {
        // S3Client의 putObject 호출 결과를 모의합니다.
        when(s3Client.putObject((PutObjectRequest) any(), (RequestBody) any())).thenReturn(PutObjectResponse.builder().build());
    }

    @Test
    void uploadImage_Success() throws Exception {
        // 업로드할 파일 모의
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes());

        // 예상되는 File 객체 생성
        String originalName = multipartFile.getOriginalFilename();
        String storedName = String.format("%s_%s", UUID.randomUUID(), originalName);
        String uploadDir = "balance-talk-images/balance-option/";
        FileType fileType = FileType.JPEG;
        long contentLength = multipartFile.getSize();

        File expectedFile = File.builder()
                .originalName(originalName)
                .storedName(storedName)
                .path(uploadDir)
                .type(fileType)
                .size(contentLength)
                .build();

        when(fileRepository.save(any(File.class))).thenReturn(expectedFile);

        // 실제 서비스 메소드 실행
        FileDto result = fileUploadService.uploadImage(multipartFile);

        // 검증
        assertEquals(originalName, result.getOriginalName());
        assertEquals(storedName, result.getStoredName());
        assertEquals(uploadDir, result.getPath());
        assertEquals(fileType, result.getType());
        assertEquals(contentLength, result.getSize());
    }
}
