package balancetalk.game.domain.repository;

import balancetalk.game.domain.Game;
import balancetalk.game.dto.GameDto.GameResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {

    Page<GameResponse> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
