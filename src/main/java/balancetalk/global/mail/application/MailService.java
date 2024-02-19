package balancetalk.global.mail.application;

import balancetalk.global.mail.dto.EmailRequest;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    @Transactional
    public void sendEmail(EmailRequest request) {
        Random random = new Random();
        String authKey = String.valueOf(random.nextInt(888888) + 111111);

        createEmailForm(request.getEmail(), authKey);
    }

    private void createEmailForm(String email, String authKey) {
        String subject = "제목";
        String text = "회원 가입을 위한 인증번호는 " + authKey + "입니다. <br/>";

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(text, true);
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }
    }
}
