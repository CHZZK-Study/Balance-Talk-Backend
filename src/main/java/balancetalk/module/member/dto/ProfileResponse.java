package balancetalk.module.member.dto;

import balancetalk.module.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ProfileResponse {
    @Schema(description = "회원 닉네임", example = "닉네임")
    private String nickname;

    @Schema(description = "회원 프로필 URL", example = "https://balance-talk-static-files.s3.ap-northeast-2.amazonaws.com/balance-talk-images/balance-option/a723b360-f42f-4dc9-be69-72904b6861d9_강아지.jpeg")
    private String profileImageUrl;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "가입일", example = "2024-02-16")
    private LocalDateTime createdAt;

    @Schema(description = "작성한 게시글 수", example = "11")
    private int postsCount;

    @Schema(description = "작성한 게시글의 받은 추천 수", example = "119")
    private int totalPostLike;

    @Schema(description = "회원 등급", example = "1")
    private int level;

    public static ProfileResponse fromEntity(Member member) {
        return ProfileResponse.builder()
                .nickname(member.getNickname())
                .createdAt(member.getCreatedAt())
                .postsCount(member.getPostCount())
                .totalPostLike(member.getPostLikes())
                //.level(member.getLevel()) TODO: 추후에 레벨 정보 추가
                .build();
    }
}
