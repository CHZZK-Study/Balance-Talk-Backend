package balancetalk.comment.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTalkPickId(Long talkPickId);

    Page<Comment> findAllByMemberEmail(String email, Pageable pageable);

    Page<Comment> findAllByTalkPickIdAndParentIsNull(Long talkPickId, Pageable pageable);

    List<Comment> findAllByTalkPickIdAndParentId(Long talkPickId, Long parentId);

}
