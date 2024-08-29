package balancetalk.vote.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("SELECT v FROM Vote v WHERE v.member.id = :memberId AND v.gameOption IS NOT NULL ORDER BY v.lastModifiedAt DESC")
    Page<Vote> findAllByMemberIdAndGameDesc(Long memberId, Pageable pageable);
}
