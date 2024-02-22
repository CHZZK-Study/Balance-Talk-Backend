package balancetalk.module.member.dto;

import balancetalk.module.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NicknameUpdate {

    @Schema(description = "업데이트 된 회원 닉네임", example = "업데이트닉네임")
    private String nickname;

    public Member toEntity() {
        return Member.builder()
                .nickname(nickname)
                .build();
    }
}
