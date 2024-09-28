package balancetalk.game.domain.repository;

import balancetalk.game.domain.MainTag;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameTagRepository extends JpaRepository<MainTag, Long> {

    Optional<MainTag> findByName(String name);

    Boolean existsByName(String mainTag);
}
