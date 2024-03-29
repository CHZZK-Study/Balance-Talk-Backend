package balancetalk.module.comment.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);

    Page<Comment> findAllByMemberEmail(String email, Pageable pageable);

    Page<Comment> findAllByPostIdAndParentIsNull(Long postId, Pageable pageable);

    List<Comment> findAllByPostIdAndParentId(Long postId, Long parentId);

    @Query("select c from Comment c left join c.likes l "
            + "where c.post.id = :postId and c.member.id in :memberIds "
            + "group by c.id "
            + "having count(l) >= :minCountForBest "
            + "order by count(l) desc")
    List<Comment> findBestCommentsByPostId(@Param("postId") Long postId,
                                           @Param("memberIds") List<Long> memberIds,
                                           @Param("minCountForBest") int minCountForBest,
                                           Pageable pageable);
}
