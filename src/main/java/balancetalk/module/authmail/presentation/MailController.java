package balancetalk.module.authmail.presentation;

import balancetalk.module.authmail.application.MailService;
import balancetalk.module.authmail.dto.EmailRequest;
import balancetalk.module.authmail.dto.EmailVerification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
@Tag(name = "email", description = "회원 가입을 위한 이메일 인증 번호 API")
public class MailController {

    private final MailService mailService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/request")
    @Operation(summary = "인증 번호 발송", description = "해당 이메일 주소로 인증 번호를 발송한다.")
    public String sendTempCode(@Valid @RequestBody EmailRequest request) {
        mailService.sendTempCode(request);
        return "인증 번호가 발송되었습니다.";
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/verify")
    @Operation(summary = "인증 번호 검증", description = "해당 이메일 주소로 보낸 인증번호와 입력한 인증번호가 일치하는지 검증한다.")
    public String verifyCode(@Valid @RequestBody EmailVerification request) {
        mailService.verifyCode(request);
        return "인증이 완료 되었습니다.";
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/password")
    @Operation(summary = "비밀 번호 찾기", description = "가입된 회원 이메일로 임시 비밀번호를 발송한다.")
    public String sendTempPassword(@Valid @RequestBody EmailRequest request) {
        mailService.sendTempPassword(request);
        return "임시 비밀번호가 발송되었습니다.";
    }
}
