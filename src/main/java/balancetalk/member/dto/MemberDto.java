package balancetalk.member.dto;

import balancetalk.file.domain.File;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class MemberDto {

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "회원가입 요청")
    public static class JoinRequest {

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

        @Schema(description = "회원 권환", example = "USER")
        private Role role;

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

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "로그인 요청")
    public static class LoginRequest {

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

        public Member toEntity() {
            return Member.builder()
                    .email(email)
                    .password(password)
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    @Schema(description = "인증이 필요한 API에서 토큰 유효성 검사를 진행")
    public static class TokenDto {

        @NotNull
        @Size(max = 30)
        @Email(regexp = "^[a-zA-Z0-9._%+-]{1,20}@[a-zA-Z0-9.-]{1,10}\\.[a-zA-Z]{2,}$")
        @Schema(description = "회원 이메일", example = "test1234@naver.com")
        private String email;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "회원 조회 응답")
    public static class MemberResponse {

        @Schema(description = "회원 id", example = "1")
        private Long id;

        @Schema(description = "회원 닉네임", example = "닉네임")
        private String nickname;

        @Schema(description = "회원 프로필 URL", example = "https://balance-talk-static-files.s3.ap-northeast-2.amazonaws.com/balance-talk-images/balance-option/a723b360-f42f-4dc9-be69-72904b6861d9_강아지.jpeg")
        private String profileImageUrl;

        @Schema(description = "가입일", example = "2024-02-16 13:37:17.391706")
        private LocalDateTime createdAt;

//    @Schema(description = "작성한 게시글 수", example = "11")
//    private int postsCount;
//
//    @Schema(description = "작성한 게시글의 받은 추천 수", example = "119")
//    private int totalPostLike;
//
//    @Schema(description = "회원 등급", example = "1")
//    private int level;

        public static MemberResponse fromEntity(Member member) {
            String profileImageUrl = Optional.ofNullable(member.getProfilePhoto())
                    .map(File::getUrl)
                    .orElse(null);

            return MemberResponse.builder()
                    .id(member.getId())
                    .nickname(member.getNickname())
                    .profileImageUrl(profileImageUrl)
//                .createdAt(member.getCreatedAt())
//                .postsCount(member.getPostCount())
//                .totalPostLike(member.getPostLikes())
                    .build();
        }
    }

}
