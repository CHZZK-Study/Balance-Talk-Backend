package balancetalk.comment.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByTalkPickId(Long talkPickId, Pageable pageable);

    @Query("SELECT c FROM Comment c LEFT JOIN c.likes l WHERE c.talkPick.id = :talkPickId GROUP BY c ORDER BY COUNT(l) DESC, c.createdAt DESC")
    List<Comment> findByTalkPickIdOrderByLikesCountDescCreatedAtDesc(@Param("talkPickId") Long talkPickId);

}
