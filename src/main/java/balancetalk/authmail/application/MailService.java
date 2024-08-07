package balancetalk.authmail.application;

import balancetalk.authmail.dto.EmailRequest;
import balancetalk.authmail.dto.EmailVerification;
import balancetalk.global.caffeine.CacheType;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MailService {

    private static final String SENDER_EMAIL = "bootsprng@gmail.com";
    private static final int AUTH_NUMBER_MIN = 0;
    private static final int AUTH_NUMBER_MAX = 6;
    private static final int RANDOM_GENERATOR_MIN = 33;
    private static final int RANDOM_GENERATOR_MAX = 126;
    private static final int RANDOM_GENERATOR_LENGTH = 10;

    private final JavaMailSender javaMailSender;
    private final CacheManager cacheManager;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    public void sendAuthenticationNumber(EmailRequest request){
        Optional<Member> member = memberRepository.findByEmail(request.getEmail());
        if (member.isPresent()) {
            throw new BalanceTalkException(ErrorCode.ALREADY_REGISTERED_EMAIL);
        }
        MimeMessage message = createTempCode(request);
        javaMailSender.send(message);
    }

    public void verifyCode(EmailVerification request) {
        Optional<Member> member = memberRepository.findByEmail(request.getEmail());
        if (member.isPresent()) {
            throw new BalanceTalkException(ErrorCode.ALREADY_REGISTERED_EMAIL);
        }

        CaffeineCache cache = (CaffeineCache) cacheManager.getCache(CacheType.TempCode.getCacheName());
        Cache<Object, Object> nativeCache = cache.getNativeCache();
        Object value = nativeCache.getIfPresent(request.getEmail());

        if (!request.getVerificationCode().equals(value)) {
            throw new BalanceTalkException(ErrorCode.AUTHORIZATION_CODE_MISMATCH);
        }
    }

    public void sendTempPassword(EmailRequest request) {
        MimeMessage message = createTempPassword(request);
        javaMailSender.send(message);
    }

    private MimeMessage createTempCode(EmailRequest request){
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
            throw new BalanceTalkException(ErrorCode.FAIL_SEND_EMAIL);
        }

        cacheManager.getCache(CacheType.TempCode.getCacheName()).put(request.getEmail(), authCode);
        return message;
    }

    private MimeMessage createTempPassword(EmailRequest request) {
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
            throw new BalanceTalkException(ErrorCode.FAIL_SEND_EMAIL);
        }
        return message;
    }

    public void updateTempPassword(String tempPwd, EmailRequest request) {
        String encodedTempPwd = passwordEncoder.encode(tempPwd);
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_MEMBER));
        member.updatePassword(encodedTempPwd);
    }

    private String createNumber(){
        return UUID.randomUUID().toString().substring(AUTH_NUMBER_MIN, AUTH_NUMBER_MAX);
    }

    private String createTempPassword() {
        SecureRandom secureRandom = new SecureRandom();
        String charNSpecialChar = IntStream.rangeClosed(RANDOM_GENERATOR_MIN, RANDOM_GENERATOR_MAX)
                .mapToObj(i -> String.valueOf((char) i))
                .collect(Collectors.joining());
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < RANDOM_GENERATOR_LENGTH; i++) {
            builder.append(charNSpecialChar.charAt(secureRandom.nextInt(charNSpecialChar.length())));

        }
        return builder.toString();
    }
}
