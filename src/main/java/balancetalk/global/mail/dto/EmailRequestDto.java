package balancetalk.global.mail.dto;

import jakarta.validation.constraints.Email;

import lombok.Data;

@Data
public class EmailRequestDto {
    @Email
    private String email;
}
