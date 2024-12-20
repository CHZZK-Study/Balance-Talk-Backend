package balancetalk.game.domain.repository;

import balancetalk.game.domain.Game;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameRepository extends JpaRepository<Game, Long> {

    @Query(value = "SELECT DISTINCT g.* " +
            "FROM game g " +
            "LEFT JOIN game_set gs ON g.game_set_id = gs.id " +
            "LEFT JOIN game_option go ON go.game_id = g.id " +
            "WHERE " +
            "gs.sub_tag = :query OR " +
            "gs.title = :query OR " +
            "g.description = :query OR " +
            "go.name = :query OR " +
            "go.description = :query",
            nativeQuery = true)
    List<Game> searchExactMatch(@Param("query") String query);

    @Query(value = "SELECT DISTINCT g.* " +
            "FROM game g " +
            "INNER JOIN game_set gs ON g.game_set_id = gs.id " +
            "INNER JOIN game_option go ON go.game_id = g.id " +
            "WHERE " +
            "MATCH(gs.sub_tag) AGAINST (:query IN NATURAL LANGUAGE MODE) OR " +
            "MATCH(gs.title) AGAINST (:query IN NATURAL LANGUAGE MODE) OR " +
            "MATCH(g.description) AGAINST (:query IN NATURAL LANGUAGE MODE) OR " +
            "MATCH(go.name) AGAINST (:query IN NATURAL LANGUAGE MODE) OR " +
            "MATCH(go.description) AGAINST (:query IN NATURAL LANGUAGE MODE)",
            nativeQuery = true)
    List<Game> searchNaturalLanguage(@Param("query") String query);
}
