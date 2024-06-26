package balancetalk.module.authmail.application;

import balancetalk.authmail.application.MailService;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.global.redis.application.RedisService;
import balancetalk.authmail.dto.EmailRequest;
import balancetalk.authmail.dto.EmailVerification;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    JavaMailSender javaMailSender;

    @Mock
    RedisService redisService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    MailService mailService;

    private final String email = "test@gmail.com";
    private static final String TEMP = "temp ";
    private final String KEY = TEMP + email;



    @Test
    @DisplayName("Redis에 저장된 유저 이메일로 올바르게 인증 번호가 조회되는지 테스트")
    void findValidRedisValueByEmail() {
        // given
        EmailVerification request = new EmailVerification(email, "123456");

        when(redisService.getValues(KEY)).thenReturn(request.getVerificationCode());

        // when, then
        assertDoesNotThrow(() -> mailService.verifyCode(request));
    }

    @Test
    @DisplayName("인증 번호가 다를 때 예외 처리 테스트")
    void incorrectAuthException() {
        // given
        EmailVerification request = new EmailVerification(email, "123456");
        String wrongNumber = "111111";
        when(redisService.getValues(KEY)).thenReturn(wrongNumber);

        // when, then
        assertThatThrownBy(() -> mailService.verifyCode(request))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessage(ErrorCode.AUTHORIZATION_CODE_MISMATCH.getMessage());

    }

    @Test
    @DisplayName("중복된 이메일일 때 예외 처리 테스트")
    void duplicateEmailException() {
        // given
        String duplicateEmail = "duplicate@gmail.com";
        Member member = Member.builder()
                .email(duplicateEmail)
                .build();

        when(memberRepository.findByEmail(duplicateEmail)).thenReturn(Optional.of(member));
        EmailRequest requestDto = new EmailRequest(duplicateEmail);

        // when, then
        assertThatThrownBy(() -> mailService.sendAuthenticationNumber(requestDto))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessage(ErrorCode.ALREADY_REGISTERED_EMAIL.getMessage());
    }

    @Test
    @DisplayName("임시 비밀번호 발송 후 회원 비밀번호가 달라지는지 테스트")
    void checkPasswordDifference() {
        // given
        Member member = Member.builder()
                .email(email)
                .password("test1234!")
                .build();
        EmailRequest request = new EmailRequest(member.getEmail());
        String tempPwd = "DHNWBANXMZ";
        String encodedTempwd = "$2a$10$RPc7MUUZlaZMX1DRWTYY9u4ecXKps2hkxOIAJt2jP88u40QT.IUSa";
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(passwordEncoder.encode(tempPwd)).thenReturn(encodedTempwd);

        // when
        mailService.updateTempPassword(tempPwd, request);

        // then
        assertEquals(encodedTempwd, member.getPassword());
        assertThat(member.getPassword()).isNotEqualTo("test1234!");
    }
}
