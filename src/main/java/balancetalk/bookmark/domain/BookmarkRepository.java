package balancetalk.bookmark.domain;

import balancetalk.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    @Query("SELECT b FROM Bookmark b WHERE b.member = :member AND b.bookmarkType = :bookmarkType AND b.active = true ORDER BY b.lastModifiedAt DESC")
    List<Bookmark> findActivatedByMemberOrderByDesc(@Param("member") Member member, @Param("bookmarkType") BookmarkType bookmarkType);

}
