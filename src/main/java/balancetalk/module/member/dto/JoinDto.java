package balancetalk.module.member.dto;

import balancetalk.module.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
//@NoArgsConstructor
@AllArgsConstructor
public class JoinDto {
    private String nickname;
    private String email;
    private String password;
    // TODO: profilePhoto 추가

    public Member toEntity() {
        return Member.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .build();
    }
}
