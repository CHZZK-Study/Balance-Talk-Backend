package balancetalk.module.notice.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.member.domain.Role;
import balancetalk.module.notice.domain.Notice;
import balancetalk.module.notice.domain.NoticeRepository;
import balancetalk.module.notice.dto.NoticeRequest;
import balancetalk.module.notice.dto.NoticeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @InjectMocks
    private NoticeService noticeService;

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private MemberRepository memberRepository;

    private final String adminEmail = "admin@example.com";
    private final String nonAdminEmail = "user@example.com";
    private final Member adminMember = Member.builder().email(adminEmail).role(Role.ADMIN).build();
    private final Member nonAdminMember = Member.builder().email(nonAdminEmail).role(Role.USER).build();

    @BeforeEach
    void setUp() {
        // SecurityContext에 인증된 사용자 설정
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // 여기에 추가
        when(authentication.getName()).thenReturn(adminEmail);
    }

    @Test
    @DisplayName("공지사항 생성 성공")
    void createNotice_Success() {
        // given
        NoticeRequest noticeRequest = new NoticeRequest("공지사항 제목", "공지사항 내용");
        when(memberRepository.findByEmail(adminEmail)).thenReturn(Optional.of(adminMember));
        when(noticeRepository.save(any(Notice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        NoticeResponse noticeResponse = noticeService.createNotice(noticeRequest);

        // then
        assertNotNull(noticeResponse);
        assertEquals(noticeRequest.getTitle(), noticeResponse.getTitle());
        assertEquals(noticeRequest.getContent(), noticeResponse.getContent());
    }

    @Test
    @DisplayName("공지사항 생성 실패 - Role이 Admin이 아닐 경우")
    void createNotice_UnauthorizedRole_Fail() {
        // given
        NoticeRequest noticeRequest = new NoticeRequest("공지사항 제목", "공지사항 내용");
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(nonAdminMember));

        // when
        // then
        assertThrows(BalanceTalkException.class, () -> noticeService.createNotice(noticeRequest));
    }

    @Test
    @DisplayName("공지사항 생성 실패 - 공지사항 제목 100자 초과 또는 공백일 경우")
    void createNotice_InvalidTitle_Fail() {
        // given
        String longTitle = "t".repeat(101);
        NoticeRequest longTitleRequest = new NoticeRequest(longTitle, "공지사항 내용");
        NoticeRequest emptyTitleRequest = new NoticeRequest("", "공지사항 내용");
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(adminMember));

        // when
        // then
        assertThrows(BalanceTalkException.class, () -> noticeService.createNotice(longTitleRequest));
        assertThrows(BalanceTalkException.class, () -> noticeService.createNotice(emptyTitleRequest));
    }

    @Test
    @DisplayName("공지사항 생성 실패 - 공지사항 내용 2000자 초과 또는 공백일 경우")
    void createNotice_InvalidContent_Fail() {
        // given
        String longContent = "c".repeat(2001);
        NoticeRequest longContentRequest = new NoticeRequest("공지사항 제목", longContent);
        NoticeRequest emptyContentRequest = new NoticeRequest("공지사항 제목", "");
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(adminMember));

        // when
        // then
        assertThrows(BalanceTalkException.class, () -> noticeService.createNotice(longContentRequest));
        assertThrows(BalanceTalkException.class, () -> noticeService.createNotice(emptyContentRequest));
    }

}