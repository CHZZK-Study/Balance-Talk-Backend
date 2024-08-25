package balancetalk.member.dto;

import balancetalk.member.domain.Member;
import balancetalk.member.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

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

        @Schema(description = "회원 권한", example = "USER")
        private Role role;

        @Schema(description = "회원 프로필 url", example = "https://pikko-image.s3.ap-northeast-2.amazonaws.com/member/511ca5c7-4367-40d1-ab18-3a8f0f4332a7_unnamed.png")
        private String profileImgUrl;

        public Member toEntity() {
            return Member.builder()
                    .nickname(nickname)
                    .email(email)
                    .password(password)
                    .role(Role.USER)
                    .profileImgUrl(profileImgUrl)
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

        @Schema(description = "회원 프로필 URL", example = "https://pikko-image.s3.ap-northeast-2.amazonaws.com/member/511ca5c7-4367-40d1-ab18-3a8f0f4332a7_unnamed.pn")
        private String profileImageUrl;

        @Schema(description = "가입일", example = "2024-02-16 13:37:17.391706")
        private LocalDateTime createdAt;

        @Schema(description = "작성한 게시글 수", example = "23")
        private int postsCount;

        @Schema(description = "저장한 게시글 수", example = "21")
        private int bookmarkedPostsCount;

//    @Schema(description = "작성한 게시글의 받은 추천 수", example = "119")
//    private int totalPostLike;
//
//    @Schema(description = "회원 등급", example = "1")
//    private int level;

        public static MemberResponse fromEntity(Member member) {
//            String profileImageUrl = Optional.ofNullable(member.getProfilePhoto())
//                    .map(File::getUrl)
//                    .orElse(null);

            return MemberResponse.builder()
                    .id(member.getId())
                    .nickname(member.getNickname())
                    .postsCount(member.getPostsCount())
                    .bookmarkedPostsCount(member.getBookmarkedPostsCount())
//                .createdAt(member.getCreatedAt())
//                .postsCount(member.getPostCount())
//                .totalPostLike(member.getPostLikes())
                    .build();
        }
    }
}
