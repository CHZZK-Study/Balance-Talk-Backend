package balancetalk.global.oauth2.dto;

import balancetalk.member.domain.Member;
import balancetalk.member.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Oauth2Dto {

    private String name;
    private String email;
    private Role role;
    private String password;

    public Member toEntity() {
        return Member.builder()
                .nickname(name)
                .email(email)
                .role(Role.USER)
                .password(password)
                .build();
    }
}
