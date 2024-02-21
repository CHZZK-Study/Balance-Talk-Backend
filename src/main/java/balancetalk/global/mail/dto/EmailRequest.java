package balancetalk.global.mail.dto;

import jakarta.validation.constraints.Email;

import lombok.Data;

@Data
public class EmailRequest {
    @Email
    private String email;
}
