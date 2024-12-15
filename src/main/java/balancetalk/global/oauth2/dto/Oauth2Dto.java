package balancetalk.global.oauth2.dto;



import static balancetalk.member.domain.Role.USER;
import static balancetalk.member.domain.SignupType.SOCIAL;

import balancetalk.member.domain.Member;
import balancetalk.member.domain.Role;
import balancetalk.member.domain.SignupType;
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
    private SignupType signupType;
    private String password;

    public Member toEntity() {
        return Member.builder()
                .nickname(name)
                .email(email)
                .role(USER)
                .signupType(SOCIAL)
                .password(password)
                .build();
    }
}
