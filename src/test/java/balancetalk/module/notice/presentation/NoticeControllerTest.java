package balancetalk.module.notice.presentation;

import balancetalk.module.notice.dto.NoticeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class NoticeControllerTest {
/*
    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("전체 공지사항 조회 실패 - 페이지 번호가 0 또는 음수")
    void findAllNotices_InvalidPage_Fail() throws Exception {
        mockMvc.perform(get("/notices")
                        .param("page", "-1")
                        .param("size", "13")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // 페이지 사이즈가 0 또는 음수일 때의 예외 처리 검증
    @Test
    @DisplayName("전체 공지사항 조회 실패 - 페이지 사이즈가 0 또는 음수")
    void findAllNotices_InvalidSize_Fail() throws Exception {
        mockMvc.perform(get("/notices")
                        .param("page", "1")
                        .param("size", "-10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("공지사항 생성 실패 - 공지사항 제목 100자 초과")
    void createNotice_Fail_ExceedTitleLength() throws Exception {
        String longTitle = "t".repeat(101);
        NoticeRequest request = NoticeRequest.builder()
                .title(longTitle)
                .content("유효한 내용")
                .build();

        mockMvc.perform(post("/notices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("입력값이 제약 조건에 맞지 않습니다."));
    }

    @Test
    @DisplayName("공지사항 생성 실패 - 공지사항 내용 2000자 초과")
    void createNotice_Fail_ExceedContentLength() throws Exception {
        String longContent = "c".repeat(2001);
        NoticeRequest request = NoticeRequest.builder()
                .title("유효한 제목")
                .content(longContent)
                .build();

        mockMvc.perform(post("/notices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("입력값이 제약 조건에 맞지 않습니다."));
    }

 */
}