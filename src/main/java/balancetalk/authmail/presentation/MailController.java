package balancetalk.authmail.presentation;

import balancetalk.authmail.dto.EmailDto.EmailRequest;
import balancetalk.authmail.dto.EmailDto.EmailVerification;
import balancetalk.authmail.dto.EmailDto.PasswordResetRequest;
import balancetalk.authmail.application.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
@Tag(name = "email", description = "회원가입 인증 및 비밀번호 재설정 API")
public class MailController {

    private final MailService mailService;

    @PostMapping("/signup/code")
    @Operation(summary = "회원 가입 인증번호 발송", description = "회원 가입 시 필요한 인증번호를 발송한다.")
    public String sendSignUpCode(@Valid @RequestBody EmailRequest request) {
        mailService.sendSignUpCode(request);
        return "회원 가입 인증 번호가 발송되었습니다.";
    }

    @PostMapping("/reset/code")
    @Operation(summary = "비밀번호 재설정 인증번호 발송", description = "비밀번호 재설정 시 필요한 인증번호를 발송한다")
    public String sendPasswordReset(@Valid @RequestBody EmailRequest request) {
        mailService.sendPasswordReset(request);
        return "비밀 번호 재설정 인증 번호가 발송되었습니다.";
    }

    @PostMapping("/verify")
    @Operation(summary = "인증 번호 검증", description = "해당 이메일 주소로 보낸 인증번호와 입력한 인증번호가 일치하는지 검증한다.")
    public String verifyCode(@Valid @RequestBody EmailVerification request) {
        mailService.verifyCode(request);
        return "인증이 완료 되었습니다.";
    }

    @PostMapping("/reset")
    @Operation(summary = "비밀번호 초기화", description = "비밀번호와 비밀번호 확인 값이 일치하는 경우 기존 비밀번호를 변경한다.")
    public String resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        mailService.resetPassword(request);
        return "비밀번호가 변경되었습니다.";
    }
}
