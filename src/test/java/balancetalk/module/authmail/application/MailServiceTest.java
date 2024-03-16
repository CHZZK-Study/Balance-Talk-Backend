package balancetalk.module.authmail.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.redis.application.RedisService;
import balancetalk.module.authmail.dto.EmailRequest;
import balancetalk.module.authmail.dto.EmailVerification;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
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

    @InjectMocks
    MailService mailService;

    @Test
    @DisplayName("이메일 인증 형식이 올바르게 생성되는지 테스트")
    void createEmailFormat() {
        // Given
        String email = "test@gmail.com";
        EmailRequest request = new EmailRequest(email);
        MimeMessage mockMimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mockMimeMessage);

        // When
        MimeMessage mimeMessage = mailService.createTempCode(request);

        // then
        assertEquals(mimeMessage, mockMimeMessage);
    }

    @Test
    @DisplayName("Redis에 저장된 유저 이메일로 올바르게 인증 번호가 조회되는지 테스트")
    void findValidRedisValueByEmail() {
        // given
        EmailVerification request = new EmailVerification("test@gmail.com", "123456");

        when(redisService.getValues(request.getEmail())).thenReturn(request.getVerificationCode());

        // when, then
        assertDoesNotThrow(() -> mailService.verifyCode(request));
    }

    @Test
    @DisplayName("인증 번호가 다를 때 예외 처리 테스트")
    void incorrectAuthException() {
        // given
        EmailVerification request = new EmailVerification("test@gmail.com", "123456");
        String wrongNumber = "111111";
        when(redisService.getValues(request.getEmail())).thenReturn(wrongNumber);

        // when, then
        assertThatThrownBy(() -> mailService.verifyCode(request))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessage("인증 번호가 일치하지 않습니다.");
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
        assertThatThrownBy(() -> mailService.sendTempCode(requestDto))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessage("이미 존재하는 이메일 입니다. 다른 이메일을 입력해주세요");
    }
}
