package balancetalk.global.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationMessage {
    FIRST_COMMENT_REPLY("내가 작성한 댓글에 첫 답글이 달렸어요!"),
    COMMENT_REPLY("내가 작성한 댓글에 %d개의 답글이 달렸어요!"),
    COMMENT_LIKE("작성한 댓글이 하트 %d개를 달성했어요!"),
    COMMENT_LIKE_100("작성한 댓글에 하트가 100개! ‘공감왕 (캐릭터) 배찌를 얻었어요!"),
    COMMENT_LIKE_1000("1000개 : 작성한 댓글에 하트가 1000개! ‘공감대왕 (캐릭터) 배찌를 얻었어요!");

    private final String message;

    public String format(Object... args) {
        return String.format(message, args);
    }
}
