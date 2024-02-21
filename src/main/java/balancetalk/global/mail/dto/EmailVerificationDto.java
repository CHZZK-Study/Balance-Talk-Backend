package balancetalk.global.mail.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class EmailVerificationDto {

    @Email
    private String email;
    private String verificationCode;
}
