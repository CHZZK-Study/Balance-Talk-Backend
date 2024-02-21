package balancetalk.global.mail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

import lombok.Data;

@Data
public class EmailRequestDto {
    @Email
    @Schema(description = "인증 번호를 받을 이메일 주소", example = "test1234@naver.com")
    private String email;
}
