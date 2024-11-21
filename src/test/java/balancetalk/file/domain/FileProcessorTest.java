package balancetalk.file.domain;

import static balancetalk.file.domain.FileType.TALK_PICK;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

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
        File result = fileProcessor.process(multipartFile, TALK_PICK);

        // then
        assertThat(result.getFileType()).isEqualTo(TALK_PICK);
        assertThat(result.getMimeType()).isEqualTo("image/png");
        assertThat(result.getUploadName()).isEqualTo(multipartFile.getOriginalFilename());
    }
}