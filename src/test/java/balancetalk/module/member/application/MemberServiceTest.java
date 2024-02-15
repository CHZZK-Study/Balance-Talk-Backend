package balancetalk.module.member.application;

import balancetalk.global.jwt.JwtTokenProvider;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.member.domain.Role;
import balancetalk.module.member.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    MemberService memberService;

    JoinDto joinDto = JoinDto.builder()
            .nickname("닉네임1")
            .email("test1234@naver.com")
            .password("Test1234test!")
            .role(Role.USER)
            .build();
    @Test
    @DisplayName("회원 가입 성공")
    void JoinMember_Success() {
        // given
        Member member = joinDto.toEntity();
        when(memberRepository.save(any())).thenReturn(member);

        // when
        Long joinId = memberService.join(joinDto);

        // then
        assertThat(joinId).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("로그인 테스트 성공")
    void LoginMember_Success() {
        // given
        LoginDto loginDto = LoginDto.builder()
                .email("test1234@naver.com")
                .password("Test1234test!")
                .build();

        Member member = joinDto.toEntity();
        when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));
        when(jwtTokenProvider.createToken(member.getEmail(), member.getRole())).thenReturn(null);
        // TODO: jwt 토큰 null -> 인증 오류

        // when
        LoginSuccessDto result = memberService.login(loginDto);

        // then
        assertThat(result.getEmail()).isEqualTo(member.getEmail());
        assertThat(result.getPassword()).isEqualTo(member.getPassword());
        assertThat(result.getRole()).isEqualTo(member.getRole());
    }

    @Test
    @DisplayName("멤버 정보 조회 성공")
    void Member_Information_Success() {
        // given
        Member member = joinDto.toEntity();

        // when
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));

        MemberResponseDto result = memberService.findById(member.getId());

        // then
        assertThat(result.getNickname()).isEqualTo(member.getNickname());
        assertThat(result.getCreatedAt()).isEqualTo(member.getCreatedAt());
        assertThat(result.getPostsCount()).isEqualTo(member.getPostCount());
        assertThat(result.getTotalPostLike()).isEqualTo(member.getPostLikes());
    }

    @Test
    @DisplayName("여러 멤버 조회 성공")
    void Member_All_Information_Success() {
        // given
        Member member1 = joinDto.toEntity();
        joinDto.setNickname("닉네임2");
        Member member2 = joinDto.toEntity();
        List<Member> members = Arrays.asList(member1, member2);

        // when
        when(memberRepository.findAll()).thenReturn(members);

        List<MemberResponseDto> result = memberService.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNickname()).isEqualTo("닉네임1");
        assertThat(result.get(1).getNickname()).isEqualTo("닉네임2");
    }

    @Test
    @DisplayName("회원 정보 수정 - 성공")
    void Member_Update_Success() {
        // given
        Member member = joinDto.toEntity();
        MemberUpdateDto memberUpdateDto = MemberUpdateDto.builder()
                .nickname("새로운닉네임")
                .password("Testcase1234!")
                .build();
        // when
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        member.updateMember(memberUpdateDto.getNickname(), memberUpdateDto.getPassword());
        when(memberRepository.save(any())).thenReturn(member);

        memberService.update(member.getId(), memberUpdateDto);

        // then
        assertThat(member.getNickname()).isEqualTo(memberUpdateDto.getNickname());
        assertThat(member.getPassword()).isEqualTo(memberUpdateDto.getPassword());
    }
}