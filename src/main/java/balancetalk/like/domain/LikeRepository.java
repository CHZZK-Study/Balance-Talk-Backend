package balancetalk.like.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByCommentIdAndMemberId(Long commentId, Long MemberId);

    boolean existsByCommentIdAndMemberId(Long commentId, Long MemberId);
}
