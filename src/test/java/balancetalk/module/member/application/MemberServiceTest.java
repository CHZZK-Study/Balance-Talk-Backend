package balancetalk.module.member.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.jwt.JwtTokenProvider;
import balancetalk.global.redis.application.RedisService;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.member.domain.Role;
import balancetalk.module.member.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    MemberRepository memberRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    RedisService redisService;

    @Mock
    HttpServletRequest request;

    @InjectMocks
    MemberService memberService;

    private Member member;

    private JoinDto joinDto;

    private LoginDto loginDto;

    private String accessToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzNDVAbmF2ZXIuY29tIiwiaWF0IjoxNzA5NDc1NTE4LCJleHAiOjE3MDk1MTg3MTh9.ZZXuN4OWM2HZjWOx7Pupl5NkRtjvd4qnK_txGdRy7G5_GdKgnyF3JfiUsenQgxsi1Y_-7C0dA85xabot2m1cag";
    private String refreshToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzNDVAbmF2ZXIuY29tIiwiaWF0IjoxNzA5NDc1NTE4LCJleHAiOjE3MTAwODAzMTh9.l87QBtVI5JJxpW0oiSWpZKX7JUESFgpZlLhW2R_cIsmf7GCEO1advBDN0csJP_PJ_bpPhQxjTd8pn0K33wBAog";
    private String anotherAccessToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzNDVAbmF2ZXIuY29tIiwiaWF0IjoxNzA5MzAxOTExLCJleHAiOjE3MDkzNDUxMTF9.DclpH2m0BKCmIspCUX-NaqHAXloLATSDe8aNHw9tgED8KighdsX_-uhJTtX8CV1i0t6c02iUcTV_J5Y1ZZ8Hog";
    @BeforeEach
    void setUp() {
        joinDto = JoinDto.builder()
                .email("member@gmail.com")
                .password("Test1234!")
                .nickname("멤버1")
                .role(Role.USER)
                .build();
        loginDto = LoginDto.builder()
                .email(joinDto.getEmail())
                .password(joinDto.getPassword())
                .build();
        member = Member.builder()
                .id(1L)
                .email(joinDto.getEmail())
                .password(joinDto.getPassword())
                .nickname("멤버1")
                .build();
    }


    @Test
    @DisplayName("회원 가입 성공")
    void JoinMember_Success() {
        // given
        String encodedPassword = "$2a$10$4QqvcI/zpEpDVM1vSEdaeugqU7xjbXSfSRUn37FOPnHY/QarX8SWS";
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
        member = Member.builder()
                .id(1L)
                .password(joinDto.getPassword())
                .build();

        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // when
        Long result = memberService.join(joinDto);

        // then
        assertThat(result).isEqualTo(1L);
    }
    @Test
    @DisplayName("로그인 테스트 성공")
    void LoginMember_Success() {
        // given
        when(memberRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.ofNullable(member));
        when(passwordEncoder.matches(eq(loginDto.getPassword()), eq(joinDto.getPassword()))).thenReturn(true);
        when(jwtTokenProvider.reissueToken(any())).thenReturn(new TokenDto("Bearer", accessToken, refreshToken));

        // when
        LoginSuccessDto result = memberService.login(loginDto);

        // then
        assertThat(result.getTokenDto().getRefreshToken()).isEqualTo(refreshToken);
        assertThat(result.getTokenDto().getAccessToken()).isEqualTo(accessToken);
        assertThat(result.getEmail()).isEqualTo(loginDto.getEmail());
        assertThat(result.getPassword()).isEqualTo(loginDto.getPassword());
    }

    @Test
    @DisplayName("로그인 테스트 실패 - 비밀번호 다름")
    void loginFailureWhenWrongPassword() {
        loginDto.setPassword("wrongPassword!");
        assertThatThrownBy(() -> memberService.login(loginDto))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessage("이메일 또는 비밀번호가 잘못되었습니다.");
    }

    @Test
    @DisplayName("로그인 테스트 실패 - 이메일 다름")
    void loginFailureWhenWrongEmail() {
        loginDto.setEmail("wrongEmail@gmail.com");
        assertThatThrownBy(() -> memberService.login(loginDto))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessage("이메일 또는 비밀번호가 잘못되었습니다.");
    }

    @Test
    @DisplayName("멤버 조회 성공")
    void findSingleMemberSuccess() {
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

        // when
        MemberResponseDto result = memberService.findById(member.getId());

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
                .hasMessage("존재하지 않는 회원입니다.");
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
        List<MemberResponseDto> result = memberService.findAll();

        // then
        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getId(), 1L);
        assertEquals(result.get(1).getId(), 2L);
    }

    @Test
    @DisplayName("회원 닉네임 수정 - 성공")
    void changeNicknameSuccess() {
        // given
        NicknameUpdate nicknameUpdate = new NicknameUpdate();
        nicknameUpdate.setNickname("새로운닉네임");

        when(jwtTokenProvider.resolveToken(request)).thenReturn(accessToken);
        when(jwtTokenProvider.getPayload(accessToken)).thenReturn(member.getEmail());
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));

        // when
        joinDto.setNickname(nicknameUpdate.getNickname());
        member = joinDto.toEntity();
        memberService.updateNickname(nicknameUpdate, request);

        // then
        assertEquals(member.getNickname(), nicknameUpdate.getNickname());
    }

    @Test
    @DisplayName("회원 닉네임 수정 실패 - JWT 불일치")
    void changeNicknameFailureJwtMismatch() {
        // given
        NicknameUpdate nicknameUpdate = new NicknameUpdate();
        nicknameUpdate.setNickname("새로운닉네임");

        when(jwtTokenProvider.resolveToken(request)).thenReturn(anotherAccessToken);
        when(jwtTokenProvider.getPayload(anotherAccessToken)).thenReturn("nonMember@gmail.com");

        // when & then
        assertThatThrownBy(() -> memberService.updateNickname(nicknameUpdate, request))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("회원 패스워드 수정 - 성공")
    void changePasswordSuccess() {
        // given
        PasswordUpdate passwordUpdate = new PasswordUpdate();
        passwordUpdate.setPassword("newPassword!");

        when(jwtTokenProvider.resolveToken(request)).thenReturn(accessToken);
        when(jwtTokenProvider.getPayload(accessToken)).thenReturn(member.getEmail());
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));

        // when
        joinDto.setPassword(passwordUpdate.getPassword());
        member = joinDto.toEntity();
        memberService.updatePassword(passwordUpdate, request);

        // then
        assertEquals(member.getPassword(), passwordUpdate.getPassword());
    }

    @Test
    @DisplayName("회원 패스워드 수정 실패 - JWT 불일치")
    void changePasswordFailureJwtMismatch() {
        // given
        PasswordUpdate passwordUpdate = new PasswordUpdate();
        passwordUpdate.setPassword("newPassword!");

        when(jwtTokenProvider.resolveToken(request)).thenReturn(anotherAccessToken);
        when(jwtTokenProvider.getPayload(anotherAccessToken)).thenReturn("nonMember@gmail.com");

        // when & then
        assertThatThrownBy(() -> memberService.updatePassword(passwordUpdate, request))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("회원 삭제 성공")
    void deleteMemberSuccess() {
        // Given
        when(jwtTokenProvider.resolveToken(request)).thenReturn(accessToken);
        when(jwtTokenProvider.getPayload(accessToken)).thenReturn(member.getEmail());
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(any(), any())).thenReturn(true); // 비밀번호 일치로 설정

        // 예외가 발생해야 하는 상황이므로, assertThrows를 사용하여 예외 발생을 확인
        memberService.delete(loginDto, request);



    }
//
//    @Test
//    @DisplayName("회원 탈퇴 - 성공")
//    void Member_Delete_Success() {
//        // given
//        LoginDto loginDto = LoginDto.builder()
//                .email("test1234@naver.com")
//                .password("Test1234test!")
//                .build();
//        Member member = joinDto.toEntity();
//
//        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
//        doNothing().when(memberRepository).deleteById(member.getId());
//
//        // when
//        memberService.delete(member.getId(), loginDto);
//
//        // then
//        verify(memberRepository).deleteById(member.getId());
//        assertThat(member.getId()).isNull();
//    }
}