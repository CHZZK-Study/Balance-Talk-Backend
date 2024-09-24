package balancetalk.game.domain.repository;

import balancetalk.game.domain.TempGameSet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempGameSetRepository extends JpaRepository<TempGameSet, Long> {
}
