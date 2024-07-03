package balancetalk.module.member.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.global.jwt.JwtTokenProvider;
import balancetalk.global.redis.application.RedisService;
import balancetalk.member.application.MemberService;
import balancetalk.file.domain.File;
import balancetalk.file.domain.FileRepository;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.MemberDto.JoinRequest;
import balancetalk.member.dto.MemberDto.LoginRequest;
import balancetalk.member.dto.MemberDto.MemberResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    MemberRepository memberRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    RedisService redisService;

    @Mock
    HttpServletRequest request;

    @Mock
    UserDetails userDetails;

    @Mock
    Authentication authentication;

    @Mock
    HttpServletResponse response;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    FileRepository fileRepository;

    @InjectMocks
    MemberService memberService;

    private Member member;

    private JoinRequest joinRequest;

    private LoginRequest loginRequest;

    private File file;

    private String accessToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzNDVAbmF2ZXIuY29tIiwiaWF0IjoxNzA5NDc1NTE4LCJleHAiOjE3MDk1MTg3MTh9.ZZXuN4OWM2HZjWOx7Pupl5NkRtjvd4qnK_txGdRy7G5_GdKgnyF3JfiUsenQgxsi1Y_-7C0dA85xabot2m1cag";
    private String refreshToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzNDVAbmF2ZXIuY29tIiwiaWF0IjoxNzA5NDc1NTE4LCJleHAiOjE3MTAwODAzMTh9.l87QBtVI5JJxpW0oiSWpZKX7JUESFgpZlLhW2R_cIsmf7GCEO1advBDN0csJP_PJ_bpPhQxjTd8pn0K33wBAog";
    private String anotherAccessToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzNDVAbmF2ZXIuY29tIiwiaWF0IjoxNzA5MzAxOTExLCJleHAiOjE3MDkzNDUxMTF9.DclpH2m0BKCmIspCUX-NaqHAXloLATSDe8aNHw9tgED8KighdsX_-uhJTtX8CV1i0t6c02iUcTV_J5Y1ZZ8Hog";

    @BeforeEach
    void setUp() {
        joinRequest = JoinRequest.builder()
                .email("member@gmail.com")
                .password("Test1234!")
                .nickname("멤버1")
                .build();
        loginRequest = LoginRequest.builder()
                .email(joinRequest.getEmail())
                .password(joinRequest.getPassword())
                .build();
        file = File.builder()
                .id(1L)
                .build();
        member = Member.builder()
                .id(1L)
                .email(joinRequest.getEmail())
                .password(joinRequest.getPassword())
                .nickname("멤버1")
                .profilePhoto(file)
                .build();

        // SecurityContext에 인증된 사용자 설정
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        lenient().when(authentication.getName()).thenReturn(member.getEmail());
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }


    @Test
    @DisplayName("회원 가입 성공")
    void JoinMember_Success() {
        // given
        String encodedPassword = "$2a$10$4QqvcI/zpEpDVM1vSEdaeugqU7xjbXSfSRUn37FOPnHY/QarX8SWS";
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
        member = Member.builder()
                .id(1L)
                .password(joinRequest.getPassword())
                .build();

        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // when
        Long result = memberService.join(joinRequest);

        // then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    @DisplayName("로그인 테스트 성공")
    void LoginMember_Success() {
        // given
        when(memberRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.ofNullable(member));
        when(passwordEncoder.matches(eq(loginRequest.getPassword()), eq(joinRequest.getPassword()))).thenReturn(true);

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword()))).thenReturn(authentication);
        when(jwtTokenProvider.createAccessToken(authentication, member.getId())).thenReturn(accessToken);
        when(jwtTokenProvider.createRefreshToken(authentication)).thenReturn(refreshToken);
        // when
        String result = memberService.login(loginRequest, response);

        // then
        assertThat(result).isEqualTo(accessToken);
    }

    @Test
    @DisplayName("로그인 테스트 실패 - 비밀번호 다름")
    void loginFailureWhenWrongPassword() {
        loginRequest.setPassword("wrongPassword!");
        assertThatThrownBy(() -> memberService.login(loginRequest, response))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessage(ErrorCode.MISMATCHED_EMAIL_OR_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("로그인 테스트 실패 - 이메일 다름")
    void loginFailureWhenWrongEmail() {
        loginRequest.setEmail("wrongEmail@gmail.com");
        assertThatThrownBy(() -> memberService.login(loginRequest, response))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessage(ErrorCode.MISMATCHED_EMAIL_OR_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("멤버 조회 성공")
    void findSingleMemberSuccess() {
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

        // when
        MemberResponse result = memberService.findById(member.getId());

        // then
        assertEquals(result.getNickname(), member.getNickname());
        assertEquals(result.getId(), member.getId());
        assertEquals(result.getCreatedAt(), member.getCreatedAt());
    }

    @Test
    @DisplayName("멤버 조회 실패 - 존재하지 않는 멤버")
    void findFailureWhenNotExistingMember() {
        when(memberRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> memberService.findById(2L))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessage(ErrorCode.NOT_FOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("여러 멤버 조회 성공")
    void findMultipleMemberSuccess() {
        // given
        Member member2 = Member.builder()
                .id(2L)
                .build();
        when(memberRepository.findAll()).thenReturn(List.of(member, member2));

        // when
        List<MemberResponse> result = memberService.findAll();

        // then
        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getId(), 1L);
        assertEquals(result.get(1).getId(), 2L);
    }

    @Test
    @DisplayName("회원 닉네임 수정 - 성공")
    void changeNicknameSuccess() {
        // given
        String newNickname = "새로운닉네임";

        when(jwtTokenProvider.resolveToken(request)).thenReturn(accessToken);
        when(jwtTokenProvider.getPayload(accessToken)).thenReturn(member.getEmail());
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));

        // when
        joinRequest.setNickname(newNickname);
        member = joinRequest.toEntity(null);
        memberService.updateNickname(newNickname, request);

        // then
        assertEquals(member.getNickname(), newNickname);
    }

    @Test
    @DisplayName("회원 닉네임 수정 실패 - JWT 불일치")
    void changeNicknameFailureJwtMismatch() {
        // given
        String newNickname = "새로운닉네임";

        when(jwtTokenProvider.resolveToken(request)).thenReturn(anotherAccessToken);
        when(jwtTokenProvider.getPayload(anotherAccessToken)).thenReturn("nonMember@gmail.com");

        // when & then
        assertThatThrownBy(() -> memberService.updateNickname(newNickname, request))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessage(ErrorCode.NOT_FOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("회원 패스워드 수정 - 성공")
    void changePasswordSuccess() {
        // given
        String newPassword = "newPassword!";

        when(jwtTokenProvider.resolveToken(request)).thenReturn(accessToken);
        when(jwtTokenProvider.getPayload(accessToken)).thenReturn(member.getEmail());
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));

        // when
        joinRequest.setPassword(newPassword);
        member = joinRequest.toEntity(null);
        memberService.updatePassword(newPassword, request);

        // then
        assertEquals(member.getPassword(), newPassword);
    }

    @Test
    @DisplayName("회원 패스워드 수정 실패 - JWT 불일치")
    void changePasswordFailureJwtMismatch() {
        // given
        String newPassword = "newPassword!";


        when(jwtTokenProvider.resolveToken(request)).thenReturn(anotherAccessToken);
        when(jwtTokenProvider.getPayload(anotherAccessToken)).thenReturn("nonMember@gmail.com");

        // when & then
        assertThatThrownBy(() -> memberService.updatePassword(newPassword, request))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessage(ErrorCode.NOT_FOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("회원 삭제 성공")
    void deleteMemberSuccess() {
        // Given
        when(jwtTokenProvider.resolveToken(request)).thenReturn(accessToken);
        when(jwtTokenProvider.getPayload(accessToken)).thenReturn(member.getEmail());

        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())).thenReturn(true);

        // when
        memberService.delete(loginRequest, request);

        // then
        verify(memberRepository, times(1)).deleteByEmail(eq(member.getEmail()));
    }

    @Test
    @DisplayName("회원 삭제 실패 - 이메일 불일치")
    void deleteMemberFailure_EmailMismatch() {
        // given
        Member stranger = Member.builder()
                .id(2L)
                .email("stranger@gmail.com")
                .build();
        when(jwtTokenProvider.resolveToken(request)).thenReturn(accessToken);
        when(jwtTokenProvider.getPayload(accessToken)).thenReturn(member.getEmail());

        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(stranger));

        // when & then
        assertThatThrownBy(() -> memberService.delete(loginRequest, request))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessage(ErrorCode.FORBIDDEN_MEMBER_DELETE.getMessage());
    }

    @Test
    @DisplayName("회원 삭제 실패 - 비밀번호 불일치")
    void deleteMemberFailure_PasswordMismatch() {
        // Given
        when(jwtTokenProvider.resolveToken(request)).thenReturn(accessToken);
        when(jwtTokenProvider.getPayload(accessToken)).thenReturn(member.getEmail());

        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())).thenReturn(false);

        // when
        assertThatThrownBy(() -> memberService.delete(loginRequest, request))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessage(ErrorCode.MISMATCHED_EMAIL_OR_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("로그아웃 - 성공")
    void logoutMemberSuccess() {
        // given,
        lenient().when(redisService.getValues(member.getEmail())).thenReturn(refreshToken);
        // when
        memberService.logout();
        // then
        verify(redisService).deleteValues(member.getEmail());
    }

    @Test
    @DisplayName("로그아웃 실패 - redis에 저장된 정보가 없음")
    void logoutFailure_RedisNull(){
        // given
        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> memberService.logout())
                .isInstanceOf(BalanceTalkException.class)
                .hasMessage(ErrorCode.AUTHENTICATION_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("닉네임 중복 검증 테스트 - 성공")
    void nickNameDuplicateVerifyTest_Success() {
        // given
        String verifyName = "체크할닉네임";
        when(memberRepository.existsByNickname(verifyName)).thenReturn(false);

        // when
        memberService.verifyNickname(verifyName);

        // then
        assertNotEquals(verifyName, member.getNickname());
    }
    @Test
    @DisplayName("닉네임 중복 검증 테스트 - 실패")
    void nickNameDuplicateVerifyTest_Fail() {
        // given
        String verifyName = "멤버1";
        when(memberRepository.existsByNickname(verifyName)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.verifyNickname(verifyName))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessage(ErrorCode.ALREADY_REGISTERED_NICKNAME.getMessage());
    }

    @Test
    @DisplayName("회원 프로필 이미지 수정 성공")
    void changeMemberProfilePhoto() {
        // given
        File updateFile = File.builder()
                .id(2L)
                .storedName("95323ff4-540c-4778-93a3-3f6aeb5121ce_test.png")
                .build();
        when(jwtTokenProvider.resolveToken(request)).thenReturn(accessToken);
        when(jwtTokenProvider.getPayload(accessToken)).thenReturn(member.getEmail());
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        when(fileRepository.findByStoredName(anyString())).thenReturn(Optional.of(updateFile));

        // when
        memberService.updateImage(updateFile.getStoredName(), request);

        // then
        assertThat(member.getProfilePhoto().getStoredName()).isEqualTo(updateFile.getStoredName());
    }
}
