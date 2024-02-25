package balancetalk.module.file.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import balancetalk.module.file.application.FileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @Test
    @DisplayName("파일 업로드 실패 - 용량 초과")
    void uploadFile_Failure_SizeExceeded() throws Exception {
        // 사이즈 제한 초과 파일 생성
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[200 * 1024 * 1024]// 200MB 파일
        );

        // 파일 업로드 요청 시뮬레이션
        mockMvc.perform(multipart("/files/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("파일 업로드 실패 - 파일 이름 길이 초과")
    void uploadFile_Failure_FileNameLengthExceeded() throws Exception {
        // 파일 이름 길이 제한 초과를 시뮬레이션하기 위한 긴 파일 이름
        String longFileName = "this_is_a_very_long_file_name_that_exceeds_the_maximum_allowed_length_of_fifty_characters.jpg";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                longFileName,
                MediaType.IMAGE_JPEG_VALUE,
                "Hello, World!".getBytes()
        );

        // 파일 업로드 요청 시뮬레이션
        mockMvc.perform(multipart("/files/upload").file(file))
                .andExpect(status().isBadRequest()); // 예외 발생 시 BadRequest (400) 상태 코드를 반환하도록 예상
    }
}
