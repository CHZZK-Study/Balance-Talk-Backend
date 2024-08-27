package balancetalk.global.notification.dto;

import balancetalk.global.notification.domain.Notification;
import balancetalk.member.domain.Member;
import balancetalk.talkpick.domain.TalkPick;
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
    public static class NotificationRequest {

        @Schema(description = "알림 제목", example = "톡픽")
        private String title;

        @Schema(description = "알림 상세 메시지", example = "작성한 댓글이 하트 10개를 달성했어요.")
        private String message;

        public static Notification toEntity(Member member, TalkPick talkPick, String title, String message) {
            return Notification.builder()
                    .member(member)
                    .title(title)
                    .resourceTitle(talkPick.getTitle())
                    .message(message)
                    .isRead(false)
                    .build();
        }
    }


    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "톡픽 알림 응답 요청")
    public static class NotificationResponse {

        @Schema(description = "알림 제목", example = "톡픽")
        private String title;

        @Schema(description = "알림 발생 날짜")
        private LocalDateTime createdAt;

        @Schema(description = "톡픽 제목", example = "이별 사유 이게 말이 돼?")
        private String talkPickTitle;

        @Schema(description = "알림 상세 메시지", example = "작성한 댓글이 하트 10개를 달성했어요.")
        private String message;

        public static NotificationResponse fromEntity(Notification notification) {
            return NotificationResponse.builder()
                    .title(notification.getTitle())
                    .createdAt(notification.getCreatedAt())
                    .talkPickTitle(notification.getResourceTitle())
                    .message(notification.getMessage())
                    .build();
        }
    }
}
