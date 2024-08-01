package balancetalk.member.application;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import balancetalk.global.jwt.JwtTokenProvider;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.member.dto.MemberDto.JoinRequest;
import balancetalk.member.dto.MemberDto.LoginRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    MemberService memberService;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    MemberRepository memberRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    HttpServletResponse response;

    @Mock
    HttpServletRequest request;

    @Mock
    ApiMember apiMember;

    JoinRequest joinRequest = JoinRequest.builder()
            .email("test@naver.com")
            .nickname("test")
            .password("rawPassword")
            .build();

    Member member = Member.builder()
            .id(1L)
            .email(joinRequest.getEmail())
            .nickname(joinRequest.getNickname())
            .build();

    @Test
    @DisplayName("회원가입을 성공적으로 완료할 때 성공적으로 memberId 값을 리턴한다.")
    void createMember_Success() {
        // given
        Mockito.when(memberRepository.existsByEmail(any())).thenReturn(false);
        Mockito.when(memberRepository.existsByNickname(any())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        Mockito.when(memberRepository.save(any())).thenReturn(member);

        // when
        Long memberId = memberService.join(joinRequest);

        // then
        Assertions.assertThat(memberId).isEqualTo(1L);
    }

    @Test
    @DisplayName("로그인을 성공적으로 완료했을 때 성공적으로 accessToken 값을 리턴한다.")
    void loginMember_Success() {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@naver.com")
                .password("rawPassword")
                .build();

        Mockito.when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));
        Mockito.when(passwordEncoder.matches(loginRequest.getPassword() , member.getPassword())).thenReturn(true);

        Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), "",
                Collections.emptyList());
        Mockito.when(jwtTokenProvider.getAuthenticationByEmail(loginRequest.getEmail())).thenReturn(authentication);
        Mockito.when(jwtTokenProvider.createAccessToken(authentication, member.getId())).thenReturn("accessToken");
        Mockito.when(jwtTokenProvider.createRefreshToken(authentication)).thenReturn("refreshToken");

        // when
        String accessToken = memberService.login(loginRequest, response);

        // then
        assertEquals(accessToken, "accessToken");
    }

    @Test
    @DisplayName("회원 닉네임 업데이트 실행시 성공적으로 변경")
    void updateMemberNickname_Success() {
        // given
        String newNickname = "newNickname";
        Mockito.when(apiMember.toMember(any())).thenReturn(member);

        // when
        memberService.updateNickname(newNickname, apiMember);

        // then
        assertEquals(member.getNickname(), newNickname);
    }

    @Test
    @DisplayName("회원 이미지 업데이트 실행 시 성공적으로 변경")
    void updateMemberImage_Success() {
        // given
        String newImgUrl = "./newImage.png";
        Mockito.when(apiMember.toMember(any())).thenReturn(member);

        // when
        memberService.updateImage(newImgUrl, apiMember);

        // then
        assertEquals(member.getProfileImgUrl(), newImgUrl);
    }

    @Test
    @DisplayName("회원 비밀번호 업데이트 시 성공적으로 변경")
    void updateMemberPassword_Success() {
        // given
        String newPassword = "newPassword";
        Mockito.when(apiMember.toMember(any())).thenReturn(member);
        Mockito.when(passwordEncoder.encode(any())).thenReturn(newPassword);

        // when
        memberService.updatePassword(newPassword, apiMember);

        // then
        assertEquals(member.getPassword(), newPassword);
    }

    @Test
    @DisplayName("회원 삭제 시 회원 DB에서 정보가 성공적으로 삭제")
    void deleteMember_Success() {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .build();
        Mockito.when(apiMember.toMember(any())).thenReturn(member);
        Mockito.when(passwordEncoder.matches(any(), any())).thenReturn(true);

        // when
        memberService.delete(loginRequest, apiMember);

        // then
        verify(memberRepository).deleteByEmail(member.getEmail());
    }

    @Test
    @DisplayName("회원 액세스 토큰이 성공적으로 재발급")
    void reIssueMemberAccessToken_Success() {
        // given
        String refreshToken = "refreshToken";
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        Cookie[] cookies = {refreshTokenCookie};
        // TODO: 비즈니스 로직 수정 후 작성예정
    }
}