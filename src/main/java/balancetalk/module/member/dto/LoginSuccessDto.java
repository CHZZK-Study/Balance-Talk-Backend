package balancetalk.module.member.dto;

import balancetalk.module.member.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginSuccessDto {
    @Schema(description = "회원 이메일", example = "test1234@naver.com")
    private String email;

    @Schema(description = "회원 비밀번호", example = "Test1234test!")
    private String password;

    @Schema(description = "회원 역할", example = "USER")
    private Role role;

    @Schema(description = "회원 가입시 발급되는 로그인 토큰", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzNEBuYXZlci5jb20iLCJyb2xlIjoiVVNFUiIsImlhdCI6MTcwODI2NTk1NSwiZXhwIjoxNzA4MjY3NzU1fQ.qUScBwxl534kzgg3nG8kd4MymWj1D2HLc1_n1dh-xBxXQFzzhI-DQd3w60U4vX1_O5mwWoYQc0hMtVY56yu9gg")
    private String token;
}
