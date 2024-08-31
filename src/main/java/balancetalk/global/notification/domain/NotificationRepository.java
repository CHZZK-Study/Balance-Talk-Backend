package balancetalk.global.notification.domain;

import balancetalk.member.domain.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByMemberAndReadStatusIsFalseOrderByCreatedAtDesc(Member member);
}
