package balancetalk.global.notification.dto;

import balancetalk.game.domain.GameSet;
import balancetalk.global.notification.domain.Notification;
import balancetalk.member.domain.Member;
import balancetalk.talkpick.domain.TalkPick;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class NotificationDto {

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "톡픽 알림 생성 요청")
    public static class TalkPickNotificationRequest {

        @Schema(description = "알림 제목", example = "톡픽")
        private String category;

        @Schema(description = "알림 상세 메시지", example = "작성한 댓글이 하트 10개를 달성했어요.")
        private String message;

        public static Notification toEntity(Member member, TalkPick talkPick, String category, String message) {
            return Notification.builder()
                    .member(member)
                    .category(category)
                    .resourceTitle(talkPick.getTitle())
                    .message(message)
                    .readStatus(false)
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "게임 알림 생성 요청")
    public static class GameNotificationRequest {

        @Schema(description = "알림 제목", example = "MY 밸런스게임")
        private String category;

        @Schema(description = "알림 상세 메시지", example = "작성한 댓글이 하트 10개를 달성했어요.")
        private String message;

        public static Notification toEntity(Member member, GameSet gameSet, String category, String message) {
            return Notification.builder()
                    .member(member)
                    .category(category)
                    // FIXME : 임시 주석처리. 밸런스게임 세트에는 제목이 없음... .resourceTitle(gameSet.ge())
                    .message(message)
                    .readStatus(false)
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "톡픽 알림 응답 요청")
    public static class NotificationResponse {

        @Schema(description = "알림 id", example = "1")
        private long id;

        @Schema(description = "알림 카테고리", example = "톡픽")
        private String category;

        @Schema(description = "알림 발생 날짜")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
        private LocalDateTime createdAt;

        @Schema(description = "게시글 제목", example = "이별 사유 이게 말이 돼?")
        private String postTitle;

        @Schema(description = "알림 상세 메시지", example = "작성한 댓글이 하트 10개를 달성했어요.")
        private String message;

        public static NotificationResponse fromEntity(Notification notification) {
            return NotificationResponse.builder()
                    .id(notification.getId())
                    .category(notification.getCategory())
                    .createdAt(notification.getCreatedAt())
                    .postTitle(notification.getResourceTitle())
                    .message(notification.getMessage())
                    .build();
        }
    }
}
