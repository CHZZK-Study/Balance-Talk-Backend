package balancetalk.game.domain.repository;

import balancetalk.game.domain.Game;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameRepository extends JpaRepository<Game, Long> {

    @Query("SELECT g FROM Game g " +
            "WHERE g.gameTopic.id IN (" +
            "    SELECT gt.id FROM GameTopic gt " +
            "    WHERE gt.name = :topicName" +
            ") " +
            "ORDER BY g.createdAt DESC")
    List<Game> findGamesByCreated(@Param("topicName") String topicName, Pageable pageable);

    @Query("SELECT g FROM Game g " +
            "WHERE g.gameTopic.id IN (" +
            "    SELECT gt.id FROM GameTopic gt " +
            "    WHERE gt.name = :topicName" +
            ") " +
            "ORDER BY g.views DESC, g.createdAt DESC")
    List<Game> findGamesByViews(@Param("topicName") String topicName, Pageable pageable);

}
