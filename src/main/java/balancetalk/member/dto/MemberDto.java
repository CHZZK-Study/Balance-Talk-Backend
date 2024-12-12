package balancetalk.member.dto;

import static balancetalk.member.domain.SignupType.*;

import balancetalk.member.domain.Member;
import balancetalk.member.domain.Role;
import balancetalk.member.domain.SignupType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class MemberDto {

    private MemberDto() {

    }

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

        @NotBlank
        @Size(min = 10, max = 20)
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{10,20}$")
        @Schema(description = "비밀번호 확인", example = "Test1234test!")
        private String passwordConfirm;

        @Schema(description = "이미지 id", example = "1")
        private Long profileImgId;

        @Schema(description = "회원 권한", example = "USER")
        private Role role;

        @Schema(description = "회원 가입 경로", example = "STANDARD")
        private SignupType signupType;

        public Member toEntity() {
            return Member.builder()
                    .nickname(nickname)
                    .email(email)
                    .password(password)
                    .role(role)
                    .signupType(STANDARD)
                    .profileImgId(profileImgId)
                    .build();
        }

        public boolean hasProfileImgId() {
            return profileImgId != null;
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

        @Schema(description = "회원 이메일", example = "test123@naver.com")
        private String email;

        @Schema(description = "회원 프로필 이미지 URL", example = "https://balancetalk.s3.ap-northeast-2.amazonaws.com/1")
        private String profileImgUrl;

        @Schema(description = "가입일", example = "2024-02-16 13:37:17.391706")
        private LocalDateTime createdAt;

        @Schema(description = "작성한 게시글 수", example = "23")
        private int postsCount;

        @Schema(description = "저장한 게시글 수", example = "21")
        private int bookmarkedPostsCount;

        public static MemberResponse fromEntity(Member member, String profileImgUrl) {
            return MemberResponse.builder()
                    .id(member.getId())
                    .nickname(member.getNickname())
                    .email(member.getEmail())
                    .profileImgUrl(profileImgUrl)
                    .createdAt(member.getCreatedAt())
                    .postsCount(member.getPostsCount())
                    .bookmarkedPostsCount(member.getBookmarkedPostsCount())
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "회원 활동 정보 응답")
    public static class MemberActivityResponse {

        @Schema(description = "회원 프로필 이미지 id", example = "1")
        private Long profileImgId;

        @Schema(description = "작성한 게시글 수", example = "23")
        private int postsCount;

        @Schema(description = "저장한 게시글 수", example = "21")
        private int bookmarkedPostsCount;

        public static MemberActivityResponse fromEntity(Member member) {
            return MemberActivityResponse.builder()
                    .profileImgId(member.getProfileImgId())
                    .postsCount(member.getPostsCount())
                    .bookmarkedPostsCount(member.getBookmarkedPostsCount())
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "회원정보 수정 요청")
    public static class MemberUpdateRequest {

        @Size(min = 2, max = 10)
        @Schema(description = "회원 닉네임", example = "닉네임")
        private String nickname;

        @Schema(description = "이미지 id", example = "1")
        private Long profileImgId;

    }
}
