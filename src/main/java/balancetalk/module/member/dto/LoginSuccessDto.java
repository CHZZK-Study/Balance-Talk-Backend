package balancetalk.module.member.dto;

import balancetalk.module.member.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginSuccessDto {
    private String email;
    private String password;
    private Role role;
    private String token;
}
