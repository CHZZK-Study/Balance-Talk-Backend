package balancetalk.game.domain.repository;

import balancetalk.game.domain.GameTopic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameTopicRepository extends JpaRepository<GameTopic, Long> {

    GameTopic findByName(String name);
}
