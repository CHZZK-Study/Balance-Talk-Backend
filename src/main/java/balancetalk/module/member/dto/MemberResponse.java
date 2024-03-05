package balancetalk.module.member.dto;

import balancetalk.module.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

    @Schema(description = "회원 id", example = "1")
    private Long id;

    @Schema(description = "회원 닉네임", example = "닉네임")
    private String nickname;

    @Schema(description = "회원 프로필", example = "../")
    private String profilePhoto; // TODO: File 타입으로 수정 예정

    @Schema(description = "가입일", example = "2024-02-16 13:37:17.391706")
    private LocalDateTime createdAt;

    @Schema(description = "작성한 게시글 수", example = "11")
    private int postsCount;

    @Schema(description = "작성한 게시글의 받은 추천 수", example = "119")
    private int totalPostLike;

    @Schema(description = "회원 등급", example = "1")
    private int level;

    public static MemberResponse fromEntity(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                //.profilePhoto(file.getPath())
                .createdAt(member.getCreatedAt())
                .postsCount(member.getPostCount())
                .totalPostLike(member.getPostLikes())
                .build();
    }
}
