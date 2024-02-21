package balancetalk.global.mail.presentation;

import balancetalk.global.mail.application.MailService;
import balancetalk.global.mail.dto.EmailRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class MailController {

    private final MailService mailService;

    @PostMapping("/request")
    public String authEmail(@Valid @RequestBody EmailRequest request) {
        int number = mailService.sendMail(request);
        return "인증 번호가 발송되었습니다.";
    }
}
