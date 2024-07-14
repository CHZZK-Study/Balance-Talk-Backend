package balancetalk.like.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByResourceIdAndMemberId(Long commentId, Long MemberId);

    boolean existsByResourceIdAndMemberId(Long commentId, Long MemberId);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.resourceId = :commentId AND l.likeType = :likeType AND l.active = true")
    int countByResourceIdAndLikeType(@Param("commentId") Long commentId, @Param("likeType") LikeType likeType);
}
