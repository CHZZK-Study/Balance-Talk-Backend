package balancetalk.vote.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VoteRepository extends JpaRepository<GameVote, Long> {

    @Query("SELECT v FROM GameVote v WHERE v.member.id = :memberId AND v.gameOption IS NOT NULL ORDER BY v.lastModifiedAt DESC")
    List<GameVote> findAllByMemberIdAndGameDesc(Long memberId);
}
