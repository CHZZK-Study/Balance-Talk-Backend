package balancetalk.module.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
@Data
public class NicknameUpdate {
    @Schema(description = "업데이트 된 회원 닉네임", example = "업데이트닉네임")
    private String nickname;

}
