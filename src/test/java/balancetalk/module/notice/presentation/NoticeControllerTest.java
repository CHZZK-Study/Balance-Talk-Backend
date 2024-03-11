package balancetalk.module.notice.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class NoticeControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
}