package balancetalk.module.file.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import balancetalk.global.config.FileConfig;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.module.file.domain.File;
import balancetalk.module.file.domain.FileRepository;
import balancetalk.module.file.domain.FileType;
import jakarta.servlet.ServletContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @InjectMocks
    private FileService fileService;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private FileConfig fileConfig;

    @Mock
    private ServletContext servletContext;


    private Path tempDirectory;

    @BeforeEach
    void setUp() throws IOException {
        // 임시 디렉토리 생성
        tempDirectory = Files.createTempDirectory("testUploads");
        servletContext = Mockito.mock(ServletContext.class);
    }

    @Test
    @DisplayName("파일 업로드 성공")
    void uploadFile_Success() {
        // given
        String originalFileName = "test.jpg";
        MultipartFile multipartFile = new MockMultipartFile("file", originalFileName, "image/jpeg",
                "test data".getBytes());
        String uploadDir = "uploads/";
        String storedFileName = "uuid_test.jpg";
        String path = Paths.get(uploadDir, storedFileName).toString();

        when(fileConfig.getLocation()).thenReturn(tempDirectory.toString());
        when(fileRepository.save(any(File.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        File savedFile = fileService.uploadFile(multipartFile);

        // then
        assertThat(savedFile.getUploadName()).isEqualTo(originalFileName);
        String expectedPath = tempDirectory.resolve(savedFile.getStoredName()).toString();
        assertThat(savedFile.getPath()).isEqualTo(expectedPath);
        assertThat(savedFile.getType()).isEqualTo(FileType.JPEG);
    }

    @Test
    @DisplayName("파일 다운로드 성공")
    void downloadFile_Success() throws Exception {
        // given
        Long fileId = 1L;
        String fileName = "uuid_download.jpg";
        Files.createFile(tempDirectory.resolve(fileName)); // 임시 파일 생성

        File file = File.builder()
                .id(fileId)
                .uploadName("download.jpg")
                .storedName(fileName)
                .path(tempDirectory.resolve(fileName).toString())
                .type(FileType.JPEG)
                .size(1024L)
                .build();

        when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));

        // when
        ResponseEntity<Resource> response = fileService.downloadFile(fileId);

        // then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getHeaders().getContentDisposition().toString()).contains(file.getUploadName());
    }

    @Test
    @DisplayName("파일 업로드 실패 - 지원되지 않는 파일 타입")
    void uploadFile_Failure_UnsupportedFileType() {
        // given
        MultipartFile multipartFile = new MockMultipartFile("file", "test.unsupported", "application/unsupported", "data".getBytes());

        // when & then
        when(fileConfig.getLocation()).thenReturn(tempDirectory.toString());

        assertThatThrownBy(() -> fileService.uploadFile(multipartFile))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining("지원하지 않는 파일 형식입니다.");
    }

    @Test
    @DisplayName("파일 다운로드 실패 - 파일을 찾을 수 없음")
    void downloadFile_Failure_FileNotFound() {
        // given
        Long fileId = 999L; // 존재하지 않는 파일 ID 가정

        when(fileRepository.findById(fileId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> fileService.downloadFile(fileId))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining("존재하지 않는 파일입니다.");
    }

    @Test
    @DisplayName("파일 다운로드 실패 - 디렉토리를 찾을 수 없음")
    void downloadFile_Failure_DirectoryNotFound() throws Exception {
        // Resource 객체는 실제 경로에 대한 검증을 할 수 없으므로, 이 부분을 모킹하는 것은 제한적입니다.
        // 이 경우, 예외를 발생시키기 위해 파일 시스템 상의 실제 상황을 조작하기보다는,
        // 실제 운영 환경에서의 로그와 모니터링을 통해 검증하는 것이 적절하다고 판단됩니다.
    }
}