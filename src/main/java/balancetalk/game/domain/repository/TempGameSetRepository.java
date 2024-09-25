package balancetalk.game.domain.repository;

import balancetalk.game.domain.TempGameSet;
import balancetalk.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempGameSetRepository extends JpaRepository<TempGameSet, Long> {
    Optional<TempGameSet> findByMember(Member member);
}
