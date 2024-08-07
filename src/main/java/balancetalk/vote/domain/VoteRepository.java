package balancetalk.vote.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("SELECT v FROM Vote v WHERE v.member.id = :memberId AND v.talkPick IS NOT NULL ORDER BY v.lastModifiedAt DESC")
    List<Vote> findAllByMemberIdDesc(Long memberId);
}
