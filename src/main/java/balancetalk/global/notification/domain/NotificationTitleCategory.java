package balancetalk.global.notification.domain;

import balancetalk.game.domain.GameSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationTitleCategory {

    WRITTEN_TALK_PICK("MY 톡픽"),
    OTHERS_TALK_PICK("톡픽"),
    WRITTEN_GAME("MY 밸런스게임"),
    OTHERS_GAME(null),
    MY_PICK("MY PICK");

    private final String category;

    public String format(Object... args) {
        return String.format(getCategory(), args);
    }

    public String getCategory(GameSet gameSet) {
        if (this == OTHERS_GAME) {
            // 동적으로 GameSet의 mainTag를 이용해 값을 설정
            return gameSet.getMainTag().getName(); // GameSet 인스턴스를 외부에서 제공받아 사용
        }
        return category;
    }
}
