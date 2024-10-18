package balancetalk.global.notification.domain;

import static balancetalk.global.exception.ErrorCode.FAIL_PARSE_NOTIFICATION_HISTORY;
import static balancetalk.global.exception.ErrorCode.FAIL_SERIALIZE_NOTIFICATION_HISTORY;

import balancetalk.global.exception.BalanceTalkException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Embeddable
public class NotificationHistory {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Column(columnDefinition = "TEXT")
    private String notificationHistory;

    public Map<String, Boolean> mappingNotification() {
        if (notificationHistory == null) {
            return new HashMap<>();
        }
        try {
            return OBJECT_MAPPER.readValue(notificationHistory, new TypeReference<Map<String, Boolean>>() {});
        } catch (IOException e) {
            throw new BalanceTalkException(FAIL_PARSE_NOTIFICATION_HISTORY);
        }
    }

    public void setNotificationHistory(Map<String, Boolean> history) {
        try {
            this.notificationHistory = OBJECT_MAPPER.writeValueAsString(history);
        } catch (IOException e) {
            throw new BalanceTalkException(FAIL_SERIALIZE_NOTIFICATION_HISTORY);
        }
    }
}
