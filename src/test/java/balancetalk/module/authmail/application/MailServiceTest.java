package balancetalk.module.authmail.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.global.redis.application.RedisService;
import balancetalk.module.authmail.dto.EmailRequest;
import balancetalk.module.authmail.dto.EmailVerification;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import jakarta.mail.internet.MimeMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

//    @BeforeEach
//    void setUp() {
//        // SecurityContext에 인증된 사용자 설정
//        Authentication authentication = mock(Authentication.class);
//        SecurityContext securityContext = mock(SecurityContext.class);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);
//
//        when(authentication.getName()).thenReturn(authenticatedEmail);
//    }
//    @AfterEach
//    void clear() {
//        SecurityContextHolder.clearContext();
//    }

    @Test
    @DisplayName("이메일 인증 형식이 올바르게 생성되는지 테스트")
    void createEmailFormat() {
        // Given
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
        assertThatThrownBy(() -> mailService.sendTempCode(requestDto))
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
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // when
        mailService.updateTempPassword(tempPwd, request);

        // then
        assertEquals(encodedTempwd, member.getPassword());
        assertThat(member.getPassword()).isNotEqualTo("test1234!");
    }
}
