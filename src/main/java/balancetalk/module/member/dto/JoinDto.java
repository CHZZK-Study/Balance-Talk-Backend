package balancetalk.module.member.dto;

import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinDto {

    @Schema(description = "회원 닉네임", example = "닉네임")
    private String nickname;

    @Schema(description = "회원 이메일", example = "test1234@naver.com")
    private String email;

    @Schema(description = "회원 비밀번호", example = "Test1234test!")
    private String password;

    @Schema(description = "회원 역할", example = "USER")
    private Role role;

    @Schema(description = "회원 ip", example = "127.0.0.1")
    private String ip;
    // TODO: profilePhoto 추가

    public Member toEntity() {
        return Member.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .role(role)
                .ip(ip)
                .build();
    }
}
