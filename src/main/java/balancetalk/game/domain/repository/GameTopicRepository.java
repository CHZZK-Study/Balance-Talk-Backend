package balancetalk.game.domain.repository;

import balancetalk.game.domain.GameTopic;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameTopicRepository extends JpaRepository<GameTopic, Long> {

    Optional<GameTopic> findByName(String name);
}
