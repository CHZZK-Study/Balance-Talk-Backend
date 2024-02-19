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
        mailService.sendEmail(request);
        return "ok";
    }

    @GetMapping
    public String test() {
        return "ok";
    }
}
