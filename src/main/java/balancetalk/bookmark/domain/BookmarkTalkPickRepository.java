package balancetalk.bookmark.domain;

import balancetalk.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookmarkTalkPickRepository extends JpaRepository<TalkPickBookmark, Long> {

    @Query("SELECT b FROM TalkPickBookmark b WHERE b.member = :member AND b.bookmarkType = :bookmarkType AND b.active = true ORDER BY b.lastModifiedAt DESC")
    Page<TalkPickBookmark> findActivatedByMemberOrderByDesc(@Param("member") Member member, @Param("bookmarkType") BookmarkType bookmarkType,
                                                            Pageable pageable);
}
