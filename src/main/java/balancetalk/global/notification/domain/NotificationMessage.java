package balancetalk.global.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationMessage {
    FIRST_COMMENT_REPLY("내가 작성한 댓글에 첫 답글이 달렸어요!"),
    COMMENT_REPLY("내가 작성한 댓글에 %d개의 답글이 달렸어요!");

    private final String message;

    public String format(Object... args) {
        return String.format(message, args);
    }
}
