package balancetalk.authmail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class EmailDto {

    private EmailDto() {

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "회원가입을 위한 인증번호 발송 요청")
    public static class EmailRequest {

        @NotNull
        @Size(max = 30)
        @Email(regexp = "^[a-zA-Z0-9._%+-]{1,20}@[a-zA-Z0-9.-]{1,10}\\.[a-zA-Z]{2,}$")
        @Schema(description = "인증 번호를 받을 이메일 주소", example = "test1234@naver.com")
        private String email;
    }

    @Data
    @AllArgsConstructor
    @Schema(description = "이메일 인증번호 검증 발송 요청")
    public static class EmailVerification {

        @NotNull
        @Size(max = 30)
        @Email(regexp = "^[a-zA-Z0-9._%+-]{1,20}@[a-zA-Z0-9.-]{1,10}\\.[a-zA-Z]{2,}$")
        @Schema(description = "인증 번호를 검증할 이메일 주소", example = "test1234@naver.com")
        private String email;

        @Schema(description = "인증 번호", example = "4f7dfb")
        private String verificationCode;
    }

    @Data
    @AllArgsConstructor
    @Schema(description = "비밀번호 재설정 요청")
    public static class PasswordResetRequest {

        @NotNull
        @Size(max = 30)
        @Email(regexp = "^[a-zA-Z0-9._%+-]{1,20}@[a-zA-Z0-9.-]{1,10}\\.[a-zA-Z]{2,}$")
        @Schema(description = "인증 번호를 검증할 이메일 주소", example = "test1234@naver.com")
        private String email;

        @NotBlank
        @Size(min = 10, max = 20)
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{10,20}$")
        @Schema(description = "비밀번호", example = "Test1234test!")
        private String password;

        @NotBlank
        @Size(min = 10, max = 20)
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{10,20}$")
        @Schema(description = "비밀번호 확인", example = "Test1234test!")
        private String passwordConfirm;
    }
}