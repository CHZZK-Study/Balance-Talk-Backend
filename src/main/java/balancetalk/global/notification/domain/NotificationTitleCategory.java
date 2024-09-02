package balancetalk.global.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationTitleCategory {

    WRITTEN_TALK_PICK("MY 톡픽"),
    OTHERS_TALK_PICK("톡픽"),
    WRITTEN_GAME("MY 밸런스게임"),
    OTHERS_GAME("밸런스게임"),
    MY_PICK("MY PICK");

    private final String category;

    public String format(Object... args) {
        return String.format(category, args);
    }
}
