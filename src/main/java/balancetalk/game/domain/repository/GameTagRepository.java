package balancetalk.game.domain.repository;

import balancetalk.game.domain.MainTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameTagRepository extends JpaRepository<MainTag, Long> {
    Boolean existsByName(String mainTag);
}
