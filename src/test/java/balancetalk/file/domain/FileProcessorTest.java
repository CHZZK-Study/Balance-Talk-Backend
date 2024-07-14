package balancetalk.file.domain;

import balancetalk.global.exception.BalanceTalkException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static balancetalk.file.domain.FileFormat.PNG;
import static balancetalk.file.domain.FileType.TALK_PICK;
import static org.assertj.core.api.Assertions.assertThat;

class FileProcessorTest {

    FileProcessor fileProcessor;

    @BeforeEach
    void setUp() {
        fileProcessor = new FileProcessor();
    }

    @Test
    @DisplayName("MultipartFile 타입의 이미지를 전달하면, 같은 속성을 가진 File 엔티티를 반환한다.")
    void process_Success_ReturnFileEntityWithSameProperties() {
        // given
        MultipartFile multipartFile =
                new MockMultipartFile("mockFile", "강아지", "image/png", "content".getBytes());

        // when
        File result = fileProcessor.process(multipartFile, "https://process.test/path/", 1L, TALK_PICK);

        // then
        assertThat(result.getResourceId()).isEqualTo(1L);
        assertThat(result.getFileType()).isEqualTo(TALK_PICK);
        assertThat(result.getFileFormat()).isEqualTo(PNG);
        assertThat(result.getPath()).isEqualTo("https://process.test/path/");
        assertThat(result.getUploadName()).isEqualTo(multipartFile.getOriginalFilename());
    }

    @Test
    @DisplayName("MultipartFile이 지원하지 않는 파일 형식이라면 예외를 발생시킨다.")
    void process_Fail_NotSupportedFileFormat() {
        // given
        MultipartFile multipartFile =
                new MockMultipartFile("mockFile", "강아지", "image/gif", "content".getBytes());

        // when, then
        Assertions.assertThatThrownBy(() ->
                        fileProcessor.process(multipartFile, "https://process.test/path/", 1L, TALK_PICK))
                .isInstanceOf(BalanceTalkException.class);
    }
}