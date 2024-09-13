package balancetalk.game.domain.repository;

import balancetalk.game.domain.Game;
import balancetalk.game.domain.GameSet;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameSetRepository extends JpaRepository<GameSet, Long> {

    @Query("SELECT g FROM GameSet g " +
            "WHERE g.mainTag.id IN (" +
            "    SELECT mt.id FROM MainTag mt " +
            "    WHERE mt.name = :name" +
            ") " +
            "ORDER BY g.createdAt DESC")
    List<Game> findGamesByCreationDate(@Param("name") String mainTag, Pageable pageable);

    @Query("SELECT g FROM GameSet g " +
            "WHERE g.mainTag.id IN (" +
            "    SELECT mt.id FROM MainTag mt " +
            "    WHERE mt.name = :name" +
            ") " +
            "ORDER BY g.views DESC, g.createdAt DESC")
    List<Game> findGamesByViews(@Param("name") String mainTag, Pageable pageable);
}
