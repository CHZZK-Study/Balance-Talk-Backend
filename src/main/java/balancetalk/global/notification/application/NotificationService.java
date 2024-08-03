package balancetalk.global.notification.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.notification.domain.Notification;
import balancetalk.global.notification.domain.NotificationRepository;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static balancetalk.global.exception.ErrorCode.SEND_NOTIFICATION_FAIL;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;
    private final Map<Long, SseEmitter> memberEmitters = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public SseEmitter createEmitter(ApiMember apiMember) {
        Long memberId = apiMember.toMember(memberRepository).getId();

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        memberEmitters.put(memberId, emitter);

        // 연결 종료 / 만료 / 에러 발생 시 emitter 제거
        emitter.onCompletion(() -> memberEmitters.remove(memberId, emitter));
        emitter.onTimeout(() -> memberEmitters.remove(memberId, emitter));
        emitter.onError((e) -> memberEmitters.remove(memberId, emitter));

        return emitter;
    }

    @Transactional
    public void sendNotification(Member member, String message) {
        Notification notification = Notification.builder()
                .member(member)
                .message(message)
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        sendRealTimeNotification(notification);
    }

    private void sendRealTimeNotification(Notification notification) {

        SseEmitter emitter = memberEmitters.get(notification.getMember().getId());

        if (emitter != null) {
            executorService.execute(() -> {
                try {
                    emitter.send(SseEmitter.event().name("notification").data(notification.getMessage()));
                } catch (Exception e) {
                    throw new BalanceTalkException(SEND_NOTIFICATION_FAIL);
                }
            });

        }
    }
}
