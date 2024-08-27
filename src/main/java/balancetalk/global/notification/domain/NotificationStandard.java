package balancetalk.global.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationStandard {
    FIRST_STANDARD_OF_NOTIFICATION(10),
    SECOND_STANDARD_OF_NOTIFICATION(50),
    THIRD_STANDARD_OF_NOTIFICATION(100),
    FOURTH_STANDARD_OF_NOTIFICATION(1000);

    private final int count;
}