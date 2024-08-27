package balancetalk.global.notification.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.notification.domain.Notification;
import balancetalk.global.notification.domain.NotificationRepository;
import balancetalk.global.notification.dto.NotificationDto.NotificationRequest;
import balancetalk.global.notification.dto.NotificationDto.NotificationResponse;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.talkpick.domain.TalkPick;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        return emitter;
    }

    @Transactional
    public void sendTalkPickNotification(Member member, TalkPick talkPick,
                                         String category, String message) {
        //TODO 여기 dto 클래스로 분리하고, 형식에 맞게 여러개 만든 다음 service 레이어에서 사용
        Notification notification = NotificationRequest.toEntity(member, talkPick, category, message);

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
}
