package balancetalk.module.member.dto;

import balancetalk.module.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PasswordUpdate {
    @Schema(description = "업데이트 된 회원 비밀번호", example = "UpdatedPassword123!")
    private String password;
}
