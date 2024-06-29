package balancetalk.member.dto;

import balancetalk.file.domain.File;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class JoinRequest {

    @NotBlank
    @Size(min = 2, max = 10)
    @Schema(description = "회원 닉네임", example = "닉네임")
    private String nickname;

    @NotNull
    @Size(max = 30)
    @Email(regexp = "^[a-zA-Z0-9._%+-]{1,20}@[a-zA-Z0-9.-]{1,10}\\.[a-zA-Z]{2,}$")
    @Schema(description = "회원 이메일", example = "test1234@naver.com")
    private String email;

    @NotBlank
    @Size(min = 10, max = 20)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{10,20}$")
    @Schema(description = "회원 비밀번호", example = "Test1234test!")
    private String password;

    @Schema(description = "회원 프로필 사진", example = "4df23447-2355-45h2-8783-7f6gd2ceb848_프로필사진.jpg")
    private String profilePhoto;

    public Member toEntity(File profilePhoto) {
        return Member.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .role(Role.USER)
                .profilePhoto(profilePhoto)
                .build();
    }
}
