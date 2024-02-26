package balancetalk.module.authmail.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.module.authmail.dto.EmailRequestDto;
import balancetalk.module.authmail.dto.EmailVerificationDto;
import balancetalk.global.redis.application.RedisService;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final RedisService redisService;
    private final MemberRepository memberRepository;

    private static final String senderEmail = "bootsprng@gmail.com";

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    private String createNumber(){
        return UUID.randomUUID().toString().substring(0, 6);
    }

    public MimeMessage createMail(EmailRequestDto request){
        String authCode = createNumber();

        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, request.getEmail());
            message.setSubject("이메일 인증");
            String body = "";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + authCode + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body,"UTF-8", "html");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        redisService.setValues(request.getEmail(), authCode, Duration.ofMillis(authCodeExpirationMillis));
        return message;
    }

    public void sendMail(EmailRequestDto request){
        validateEmail(request.getEmail());
        MimeMessage message = createMail(request);
        javaMailSender.send(message);
    }

    public void verifyCode(EmailVerificationDto request) {
        validateEmail(request.getEmail());
        String redisValue = redisService.getValues(request.getEmail());
        Optional.ofNullable(redisValue)
                .filter(code -> code.equals(request.getVerificationCode()))
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.AUTHORIZATION_CODE_MISMATCH));
    }

    private void validateEmail(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent()) {
            throw new BalanceTalkException(ErrorCode.DUPLICATE_EMAIL);
        }
    }
}
