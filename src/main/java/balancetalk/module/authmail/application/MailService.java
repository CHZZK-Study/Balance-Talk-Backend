package balancetalk.module.authmail.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.module.authmail.dto.EmailRequest;
import balancetalk.module.authmail.dto.EmailVerification;
import balancetalk.global.redis.application.RedisService;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private static final String TEMP = "temp ";
    private static final String SENDER_EMAIL = "bootsprng@gmail.com";

    private final JavaMailSender javaMailSender;
    private final RedisService redisService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    public MimeMessage createTempCode(EmailRequest request){
        String authCode = createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.setFrom(SENDER_EMAIL);
            message.setRecipients(MimeMessage.RecipientType.TO, request.getEmail());
            message.setSubject("[BalanceTalk] 인증 번호");
            String body = "";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + authCode + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body,"UTF-8", "html");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        // 로그인 할 때 유저 이메일을 key로 토큰이 저장되기 때문에 key 중복
        String key = TEMP + request.getEmail();
        redisService.setValues(key, authCode, Duration.ofMillis(authCodeExpirationMillis));
        return message;
    }

    public MimeMessage createTempPassword(EmailRequest request) {
        String tempPwd = createTempPassword();
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.setFrom(SENDER_EMAIL);
            message.setRecipients(MimeMessage.RecipientType.TO, request.getEmail());
            message.setSubject("[BalanceTalk] 임시 비밀번호");

            String body = "";
            body += "<h3>" + "임시 비밀번호 입니다." + "</h3>";
            body += "<h1>" + tempPwd + "</h1>";
            body += "<h3>" + "로그인 후에 새로운 비밀번호로 변경하셔야 합니다." + "</h3>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body,"UTF-8", "html");
            updateTempPassword(tempPwd, request);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    @Transactional
    public void updateTempPassword(String tempPwd, EmailRequest request) {
        String encodedTempPwd = passwordEncoder.encode(tempPwd);
        Member member = memberRepository.findByEmail(request.getEmail()).get();
        member.updatePassword(encodedTempPwd);
        memberRepository.save(member);
    }

    public void sendTempCode(EmailRequest request){
        validateEmail(request.getEmail());
        MimeMessage message = createTempCode(request);
        javaMailSender.send(message);
    }

    public void sendTempPassword(EmailRequest request) {
        if (!memberRepository.existsByEmail(request.getEmail())) {
            throw new BalanceTalkException(ErrorCode.NOT_FOUND_MEMBER);
        }
        MimeMessage message = createTempPassword(request);
        javaMailSender.send(message);
    }

    public void verifyCode(EmailVerification request) {
        String key = TEMP + request.getEmail();
        validateEmail(key);
        String redisValue = redisService.getValues(key);
        Optional.ofNullable(redisValue)
                .filter(code -> code.equals(request.getVerificationCode()))
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.AUTHORIZATION_CODE_MISMATCH));
    }


    private void validateEmail(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent()) {
            throw new BalanceTalkException(ErrorCode.ALREADY_REGISTERED_EMAIL);
        }
    }
    private String createNumber(){
        return UUID.randomUUID().toString().substring(0, 6);
    }

    private String createTempPassword() {
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        String tempPwd = "";

        int idx = 0;
        for (int i=0; i<10; i++) {
            idx = (int) (charSet.length * Math.random());
            tempPwd += charSet[idx];
        }
        return tempPwd;
    }
}
