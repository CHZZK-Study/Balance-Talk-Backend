package balancetalk.global.notification.presentation;

import balancetalk.global.notification.application.NotificationService;
import balancetalk.global.utils.AuthPrincipal;
import balancetalk.member.dto.ApiMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@Tag(name = "notification", description = "알림 API")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    @Operation(summary = "알림 스트리밍", description = "로그인한 사용자의 알림을 실시간으로 스트리밍합니다.")
    public SseEmitter streamNotifications(@Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {
        return notificationService.createEmitter(apiMember);
    }

    @PostMapping("/notifications/{id}/read")
    @Operation(summary = "알림 읽음 처리", description = "알림을 읽음 상태로 변경합니다.")
    public void markNotificationAsRead(@PathVariable Long id) {
        notificationService.markNotificationAsRead(id);
    }
}
