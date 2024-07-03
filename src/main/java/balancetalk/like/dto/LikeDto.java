package balancetalk.like.dto;

import balancetalk.comment.domain.Comment;
import balancetalk.like.domain.Like;
import balancetalk.like.domain.LikeType;
import balancetalk.member.domain.Member;
import balancetalk.talkpick.domain.TalkPick;
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
    public static class Request { // TODO : 추후 likeType 별로 분리 필요

        @Schema(description = "좋아요 타입", example = "TALK_PICK")
        private LikeType likeType;

        @Schema(description = "좋아요한 댓글 id", example = "1")
        private Long commentId;

        @Schema(description = "좋아요한 톡픽 id", example = "1")
        private Long talkPickId;

        public static Like toCommentLikeEntity(Comment comment, Member member) {
            return Like.builder()
                    .likeType(LikeType.COMMENT)
                    .comment(comment)
                    .member(member)
                    .active(true)
                    .build();
        }
    }

        @Data
        @Builder
        @AllArgsConstructor
        @JsonInclude
        public static class Response {

            @Schema(description = "좋아요 id", example = "1")
            private Long id;

            @Schema(description = "좋아요 타입", example = "TALK_PICK")
            private LikeType likeType;

            @Schema(description = "좋아요한 멤버 id", example = "1")
            private Long memberId;

            @Schema(description = "좋아요한 댓글 id", example = "1")
            private Long commentId;

            @Schema(description = "좋아요한 톡픽 id", example = "1")
            private Long talkPickId;

            @Schema(description = "좋아요 생성 날짜")
            private LocalDateTime createdAt;

            @Schema(description = "좋아요 변경 날짜")
            private LocalDateTime lastModifiedAt;

            public static Response fromEntity(Like like) {
                return Response.builder()
                        .id(like.getId())
                        .likeType(like.getLikeType())
                        .memberId(like.getMember().getId())
                        .commentId(Optional.ofNullable(like.getComment()).map(Comment::getId).orElse(null))
                        .talkPickId(Optional.ofNullable(like.getTalkPick()).map(TalkPick::getId).orElse(null))
                        .createdAt(like.getCreatedAt())
                        .lastModifiedAt(like.getLastModifiedAt())
                        .build();
            }
    }
}
