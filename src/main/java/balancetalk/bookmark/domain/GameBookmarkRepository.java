package balancetalk.bookmark.domain;

import balancetalk.member.domain.Member;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameBookmarkRepository extends JpaRepository<GameBookmark, Long> {

    @Query("SELECT b FROM GameBookmark b WHERE b.member = :member AND b.active = true ORDER BY b.lastModifiedAt DESC")
    Page<GameBookmark> findActivatedByMemberOrderByDesc(@Param("member") Member member, Pageable pageable);

    Optional<GameBookmark> findByMemberAndGameSetId(Member member, Long gameSetId);

}
