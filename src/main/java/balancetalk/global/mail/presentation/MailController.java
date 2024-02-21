package balancetalk.global.mail.presentation;

import balancetalk.global.mail.application.MailService;
import balancetalk.global.mail.dto.EmailRequestDto;
import balancetalk.global.mail.dto.EmailVerificationDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class MailController {

    private final MailService mailService;
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/request")
    public String authEmail(@Valid @RequestBody EmailRequestDto request) {
        mailService.sendMail(request);
        return "인증 번호가 발송되었습니다.";
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/verify")
    public String verifyCode(@Valid @RequestBody EmailVerificationDto request) {
        mailService.verifyCode(request);
        return "인증이 완료 되었습니다.";
    }
}
