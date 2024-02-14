package balancetalk.module.member.dto;

import balancetalk.module.file.domain.File;
import balancetalk.module.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto {
    private String nickname;
    private String profilePhoto;
    private LocalDateTime createdAt;
    private int postsCount;
    private int totalPostLike;
    private int level;

    public static MemberResponseDto fromEntity(Member member) {
        return MemberResponseDto.builder()
                .nickname(member.getNickname())
                //.profilePhoto(file.getPath())
                .createdAt(member.getCreatedAt())
                .postsCount(member.getPostCount())
                .totalPostLike(member.getPostLikes())
                .build();
    }
}
