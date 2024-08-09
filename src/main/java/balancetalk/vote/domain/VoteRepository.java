package balancetalk.vote.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("SELECT v FROM Vote v WHERE v.member.id = :memberId AND v.talkPick IS NOT NULL ORDER BY v.lastModifiedAt DESC")
    List<Vote> findAllByMemberIdAndTalkPickDesc(Long memberId);

    @Query("SELECT v FROM Vote v WHERE v.member.id = :memberId AND v.game IS NOT NULL ORDER BY v.lastModifiedAt DESC")
    List<Vote> findAllByMemberIdAndGameDesc(Long memberId);
}
