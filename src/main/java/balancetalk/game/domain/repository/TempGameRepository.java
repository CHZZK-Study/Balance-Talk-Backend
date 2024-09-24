package balancetalk.game.domain.repository;

import balancetalk.game.domain.TempGame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempGameRepository extends JpaRepository<TempGame, Long> {

}
