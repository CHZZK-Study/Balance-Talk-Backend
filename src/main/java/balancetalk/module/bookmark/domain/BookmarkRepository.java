package balancetalk.module.bookmark.domain;

import balancetalk.module.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByMember(Member member);
    Optional<Bookmark> findByMemberAndPostId(Member member, Long postId);

    Page<Bookmark> findAllByMemberId(Long memberId, Pageable pageable);

}
