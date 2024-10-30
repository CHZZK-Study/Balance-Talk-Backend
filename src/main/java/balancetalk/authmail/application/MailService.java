package balancetalk.authmail.application;

import balancetalk.authmail.dto.EmailDto.EmailRequest;
import balancetalk.authmail.dto.EmailDto.EmailVerification;
import balancetalk.authmail.dto.EmailDto.PasswordResetRequest;
import balancetalk.global.caffeine.CacheType;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

import static jakarta.mail.Message.RecipientType.TO;

@Service
@RequiredArgsConstructor
@Transactional
public class MailService {

    private static final String SENDER_EMAIL = "bootsprng@gmail.com";
    private static final int AUTH_NUMBER_MIN = 0;
    private static final int AUTH_NUMBER_MAX = 6;

    private final JavaMailSender javaMailSender;
    private final CacheManager cacheManager;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void sendAuthenticationNumber(EmailRequest request) {
        MimeMessage message = createTempCode(request);
        javaMailSender.send(message);
    }

    private String createNumber() {
        return UUID.randomUUID().toString().substring(AUTH_NUMBER_MIN, AUTH_NUMBER_MAX);
    }

    private MimeMessage createTempCode(EmailRequest request) {
        String authCode = createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.setFrom(SENDER_EMAIL);
            message.setRecipients(TO, request.getEmail());
            message.setSubject("[BalanceTalk] 인증 번호");
            String body = "";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + authCode + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body, "UTF-8", "html");
        } catch (MessagingException e) {
            throw new BalanceTalkException(ErrorCode.FAIL_SEND_EMAIL);
        }

        Optional.ofNullable(cacheManager.getCache(CacheType.TempCode.getCacheName()))
                .ifPresentOrElse(
                        cache -> cache.put(request.getEmail(), authCode),
                        () -> {
                            throw new BalanceTalkException(ErrorCode.CACHE_NOT_FOUND);
                        });
        return message;
    }

    public void verifyCode(EmailVerification request) {
        CaffeineCache cache = (CaffeineCache) cacheManager.getCache(CacheType.TempCode.getCacheName());
        Cache<Object, Object> nativeCache = Optional.ofNullable(cache)
                .map(CaffeineCache::getNativeCache)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.CACHE_NOT_FOUND));
        Object value = nativeCache.getIfPresent(request.getEmail());

        String verificationCode = request.getVerificationCode();
        if (!verificationCode.equals(value)) {
            throw new BalanceTalkException(ErrorCode.AUTHORIZATION_CODE_MISMATCH);
        }
    }

    public void resetPassword(PasswordResetRequest request) {
        String password = request.getPassword();
        String passwordConfirm = request.getPasswordConfirm();
        if (!password.equals(passwordConfirm)) {
            throw new BalanceTalkException(ErrorCode.PASSWORD_MISMATCH);
        }

        String email = request.getEmail();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_MEMBER));

        member.updatePassword(passwordEncoder.encode(password));
    }
}
