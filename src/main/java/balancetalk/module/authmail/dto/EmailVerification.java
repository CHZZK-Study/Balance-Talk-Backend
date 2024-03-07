package balancetalk.module.authmail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class EmailVerification {

    @Email
    @Schema(description = "인증 번호를 검증할 이메일 주소", example = "test1234@naver.com")
    private String email;

    @Schema(description = "인증 번호", example = "4f7dfb")
    private String verificationCode;
}
