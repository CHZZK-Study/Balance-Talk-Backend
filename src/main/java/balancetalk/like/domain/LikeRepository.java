package balancetalk.like.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Like findByCommentIdAndMemberId(Long commentId, Long MemberId);

    boolean existsByCommentIdAndMemberId(Long commentId, Long MemberId);
}
