package balancetalk.module.member.dto;

import balancetalk.module.member.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class LoginResponse {

    @Schema(description = "회원 이메일", example = "test1234@naver.com")
    private String email;

    @Schema(description = "회원 비밀번호", example = "Test1234test!")
    private String password;

    @Schema(description = "회원 역할", example = "USER")
    private Role role;

    private TokenDto tokenDto;

}
