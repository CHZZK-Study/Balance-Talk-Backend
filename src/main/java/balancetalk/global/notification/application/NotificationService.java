package balancetalk.global.notification.application;

import balancetalk.game.domain.Game;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.notification.domain.Notification;
import balancetalk.global.notification.domain.NotificationRepository;
import balancetalk.global.notification.dto.NotificationDto.GameNotificationRequest;
import balancetalk.global.notification.dto.NotificationDto.TalkPickNotificationRequest;
import balancetalk.global.notification.dto.NotificationDto.NotificationResponse;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.talkpick.domain.TalkPick;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static balancetalk.global.exception.ErrorCode.NOT_FOUND_MEMBER;
import static balancetalk.global.exception.ErrorCode.NOT_FOUND_NOTIFICATION;
import static balancetalk.global.exception.ErrorCode.SEND_NOTIFICATION_FAIL;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;
    private final Map<Long, SseEmitter> memberEmitters = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ObjectMapper objectMapper;

    public SseEmitter createEmitter(ApiMember apiMember) {
        Long memberId = apiMember.toMember(memberRepository).getId();

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        memberEmitters.put(memberId, emitter);

        // 연결 종료 / 만료 / 에러 발생 시 emitter 제거
        emitter.onCompletion(() -> memberEmitters.remove(memberId, emitter));
        emitter.onTimeout(() -> memberEmitters.remove(memberId, emitter));
        emitter.onError((e) -> memberEmitters.remove(memberId, emitter));

        // 연결 시 미확인 알림 전송
        sendUnreadNotifications(memberId, emitter);

        return emitter;
    }

    @Transactional
    public void sendTalkPickNotification(Member member, TalkPick talkPick,
                                         String category, String message) {
        //TODO 여기 dto 클래스로 분리하고, 형식에 맞게 여러개 만든 다음 service 레이어에서 사용
        Notification notification = TalkPickNotificationRequest.toEntity(member, talkPick, category, message);

        notificationRepository.save(notification);

        sendRealTimeNotification(notification);
    }

    @Transactional
    public void sendGameNotification(Member member, Game game,
                                         String category, String message) {
        //TODO 여기 dto 클래스로 분리하고, 형식에 맞게 여러개 만든 다음 service 레이어에서 사용
        Notification notification = GameNotificationRequest.toEntity(member, game, category, message);

        notificationRepository.save(notification);

        sendRealTimeNotification(notification);
    }

    private void sendRealTimeNotification(Notification notification) {
        SseEmitter emitter = memberEmitters.get(notification.getMember().getId());

        if (emitter != null) {
            executorService.execute(() -> {
                try {
                    NotificationResponse response = NotificationResponse.fromEntity(notification);
                    String jsonMessage = objectMapper.writeValueAsString(response);
                    emitter.send(SseEmitter.event().name("notification").data(jsonMessage));
                } catch (Exception e) {
                    throw new BalanceTalkException(SEND_NOTIFICATION_FAIL);
                }
            });
        }
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(Member member) {
        return notificationRepository.findAllByMemberAndReadStatusIsFalseOrderByCreatedAtDesc(member);
    }

    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_NOTIFICATION));
        notification.read();
        notificationRepository.save(notification);
    }

    private void sendUnreadNotifications(Long memberId, SseEmitter emitter) {
        List<Notification> unreadNotifications = notificationRepository
                .findAllByMemberAndReadStatusIsFalseOrderByCreatedAtDesc(memberRepository.findById(memberId)
                        .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_MEMBER))
        );

        unreadNotifications.forEach(notification -> {
            try {
                NotificationResponse response = NotificationResponse.fromEntity(notification);
                String jsonMessage = objectMapper.writeValueAsString(response);
                emitter.send(SseEmitter.event().name("notification").data(jsonMessage));
            } catch (Exception e) {
                log.error("Failed to send unread notification: ", e);
            }
        });
    }
}
