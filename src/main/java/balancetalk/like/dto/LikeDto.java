package balancetalk.like.dto;

import balancetalk.like.domain.LikeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Response { // TODO : 추후 likeType 별로 분리 필요

    }
}
