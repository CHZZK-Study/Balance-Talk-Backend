package balancetalk.module.member.dto;

import balancetalk.module.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateDto {
    private String nickname;
    private String password;

    public Member toEntity() {
        return Member.builder()
                .nickname(nickname)
                .password(password)
                .build();
    }
}
