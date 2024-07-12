package balancetalk.like.dto;

import balancetalk.comment.domain.Comment;
import balancetalk.like.domain.Like;
import balancetalk.like.domain.LikeType;
import balancetalk.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;

public class LikeDto {

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "좋아요 생성 요청")
    public static class CreateLikeRequest {

        @Schema(description = "좋아요 타입", example = "COMMENT")
        private LikeType likeType;

        @Schema(description = "좋아요한 댓글 id", example = "1")
        private Long commentId;

        public static Like toEntity(Long resourceId, Member member) {
            return Like.builder()
                    .likeType(LikeType.COMMENT)
                    .resourceId(resourceId)
                    .member(member)
                    .active(true)
                    .build();
        }
    }

        @Data
        @Builder
        @AllArgsConstructor
        @JsonInclude
        @Schema(description = "좋아요 조회 응답 (현재 미사용)")
        public static class LikeResponse {

            @Schema(description = "좋아요 id", example = "1")
            private Long id;

            @Schema(description = "좋아요 타입", example = "COMMENT")
            private LikeType likeType;

            @Schema(description = "좋아요한 멤버 id", example = "1")
            private Long memberId;

            @Schema(description = "좋아요한 댓글 id", example = "1")
            private Long resourceId;

            @Schema(description = "좋아요 생성 날짜")
            private LocalDateTime createdAt;

            @Schema(description = "좋아요 변경 날짜")
            private LocalDateTime lastModifiedAt;

            public static LikeResponse fromEntity(Like like) {
                return LikeResponse.builder()
                        .id(like.getId())
                        .likeType(like.getLikeType())
                        .memberId(like.getMember().getId())
                        .resourceId(like.getResourceId())
                        .createdAt(like.getCreatedAt())
                        .lastModifiedAt(like.getLastModifiedAt())
                        .build();
            }
    }
}
