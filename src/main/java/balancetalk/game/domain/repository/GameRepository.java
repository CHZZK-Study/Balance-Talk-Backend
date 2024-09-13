package balancetalk.game.domain.repository;

import balancetalk.game.domain.Game;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameRepository extends JpaRepository<Game, Long> {

//    Page<Game> findAllByMemberIdOrderByEditedAtDesc(Long memberId, Pageable pageable);
}
