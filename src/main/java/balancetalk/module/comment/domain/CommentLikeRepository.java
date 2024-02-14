package balancetalk.module.comment.domain;

import balancetalk.module.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {


    boolean existsByMemberAndComment(Member member, Comment comment);
}
