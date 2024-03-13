package balancetalk.module.notice.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.module.file.domain.File;
import balancetalk.module.file.domain.FileRepository;
import balancetalk.module.file.domain.FileType;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.member.domain.Role;
import balancetalk.module.notice.domain.Notice;
import balancetalk.module.notice.domain.NoticeRepository;
import balancetalk.module.notice.dto.NoticeRequest;
import balancetalk.module.notice.dto.NoticeResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Mock
    private FileRepository fileRepository;

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
        lenient().when(authentication.getName()).thenReturn(adminEmail);
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("공지사항 생성 성공")
    void createNotice_Success() {
        // given
        NoticeRequest noticeRequest = new NoticeRequest("공지사항 제목", "공지사항 내용", List.of("file1.jpg", "file2.jpg"));
        List<File> mockFiles = noticeRequest.getStoredFileNames().stream()
                .map(fileName -> File.builder()
                        .originalName(fileName)
                        .storedName(fileName)
                        .path("path/to/" + fileName)
                        .size(123L) // 예시 사이즈
                        .type(FileType.JPEG) // 예시 파일 타입
                        // Notice 설정은 나중에 setNotice로 할 것이므로 여기서는 생략
                        .build())
                .collect(Collectors.toList());
        when(memberRepository.findByEmail(adminEmail)).thenReturn(Optional.of(adminMember));
        when(noticeRepository.save(any(Notice.class))).thenAnswer(invocation -> invocation.getArgument(0));
        mockFiles.forEach(file -> when(fileRepository.findByStoredName(file.getStoredName())).thenReturn(Optional.of(file)));
        when(fileRepository.saveAll(anyList())).thenReturn(mockFiles);

        // when
        NoticeResponse noticeResponse = noticeService.createNotice(noticeRequest);

        // then
        assertNotNull(noticeResponse);
        assertEquals(noticeRequest.getTitle(), noticeResponse.getTitle());
        assertEquals(noticeRequest.getContent(), noticeResponse.getContent());
        assertEquals(2, noticeResponse.getStoredFileNames().size());
    }

    @Test
    @DisplayName("전체 공지 조회 성공")
    void getAllNotices_Success() {
        // given
        Notice notice = Notice.builder()
                .id(1L)
                .title("공지사항 제목")
                .content("공지사항 내용")
                .member(adminMember)
                .build();
        Page<Notice> page = new PageImpl<>(Collections.singletonList(notice));
        when(noticeRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(fileRepository.findByNoticeId(anyLong())).thenReturn(Collections.emptyList()); // 파일 목록 없음을 가정

        // when
        Page<NoticeResponse> result = noticeService.findAllNotices(PageRequest.of(0, 10));

        // then
        assertFalse(result.isEmpty());
        assertEquals(1, result.getContent().size());
        assertTrue(result.getContent().get(0).getStoredFileNames().isEmpty()); // 파일 목록이 비어있음을 확인
    }

    @Test
    @DisplayName("특정 공지 조회 성공")
    void findNoticeById_Success() {
        // given
        Long noticeId = 1L;
        Notice notice = Notice.builder()
                .id(noticeId)
                .title("특정 공지")
                .content("내용")
                .member(adminMember)
                .build();

        when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(notice));

        // when
        NoticeResponse result = noticeService.findNoticeById(noticeId);

        // then
        assertNotNull(result);
        assertEquals(noticeId, result.getId());
        assertEquals("특정 공지", result.getTitle());
    }

    @Test
    @DisplayName("공지사항 수정 성공")
    void updateNotice_Success() {
        // given
        Long noticeId = 1L;
        Notice originalNotice = Notice.builder()
                .id(noticeId)
                .title("기존 제목")
                .content("기존 내용")
                .member(adminMember)
                .build();
        List<String> mockFileNames = List.of("updatedFile.jpg"); // 수정된 파일 이름 목록

        when(memberRepository.findByEmail(adminEmail)).thenReturn(Optional.of(adminMember));
        when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(originalNotice));
        when(noticeRepository.save(any(Notice.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(fileRepository.findByNoticeId(noticeId)).thenReturn(mockFileNames.stream()
                .map(fileName -> File.builder().storedName(fileName).notice(originalNotice).build())
                .collect(Collectors.toList())); // 수정된 파일 목록을 제공

        NoticeRequest updateRequest = new NoticeRequest("새 제목", "새 내용", mockFileNames); // 수정된 파일 이름 목록을 포함하여 요청 객체 생성

        // when
        NoticeResponse updatedNotice = noticeService.updateNotice(noticeId, updateRequest);

        // then
        assertEquals("새 제목", updatedNotice.getTitle());
        assertEquals("새 내용", updatedNotice.getContent());
        assertEquals(mockFileNames.size(), updatedNotice.getStoredFileNames().size()); // 파일 목록에 파일이 정확히 있음을 확인
        assertEquals(mockFileNames.get(0), updatedNotice.getStoredFileNames().get(0)); // 실제 파일 이름이 예상과 일치하는지 확인
    }

    @Test
    @DisplayName("공지사항 삭제 성공")
    void deleteNoticeById_Success() {
        // given
        Long noticeId = 1L;
        Notice notice = Notice.builder()
                .id(noticeId)
                .title("특정 공지")
                .content("내용")
                .member(adminMember)
                .build();

        when(memberRepository.findByEmail(adminEmail)).thenReturn(Optional.of(adminMember));
        when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(notice));
        doNothing().when(noticeRepository).delete(any(Notice.class));

        // when
        noticeService.deleteNotice(noticeId);

        // then
        verify(noticeRepository).delete(notice);
    }

    @Test
    @DisplayName("공지사항 생성 실패 - Role이 Admin이 아닐 경우")
    void createNotice_UnauthorizedRole_Fail() {
        // given
        NoticeRequest noticeRequest = new NoticeRequest("공지사항 제목", "공지사항 내용", Collections.emptyList());
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(nonAdminMember));

        // when
        // then
        assertThrows(BalanceTalkException.class, () -> noticeService.createNotice(noticeRequest));
    }

    @Test
    @DisplayName("특정 공지 조회 실패 - 공지사항 존재하지 않음")
    void findNoticeById_NotFound_Fail() {
        // given
        Long invalidNoticeId = 999L;
        when(noticeRepository.findById(invalidNoticeId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(BalanceTalkException.class, () -> noticeService.findNoticeById(invalidNoticeId));
    }

    @Test
    @DisplayName("공지사항 수정 실패 - 존재하지 않는 공지사항")
    void updateNotice_NotFound_Fail() {
        // given
        Long invalidNoticeId = 999L;
        when(noticeRepository.findById(invalidNoticeId)).thenReturn(Optional.empty());
        NoticeRequest updateRequest = new NoticeRequest("제목", "내용", Collections.emptyList()); // 빈 파일 목록 포함

        // when & then
        assertThrows(BalanceTalkException.class, () -> noticeService.updateNotice(invalidNoticeId, updateRequest));
    }

    // 특정 공지사항이 존재하지 않을 경우 삭제 시 예외 발생
    @Test
    @DisplayName("공지사항 삭제 실패 - 존재하지 않는 공지사항")
    void deleteNoticeById_NotFound_Fail() {
        // given
        Long invalidNoticeId = 999L;
        when(noticeRepository.findById(invalidNoticeId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(BalanceTalkException.class, () -> noticeService.deleteNotice(invalidNoticeId));
    }
}