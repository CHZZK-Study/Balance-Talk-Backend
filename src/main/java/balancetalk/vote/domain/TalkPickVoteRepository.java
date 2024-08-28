package balancetalk.vote.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TalkPickVoteRepository extends JpaRepository<TalkPickVote, Long> {

    @Query("SELECT v FROM TalkPickVote v WHERE v.member.id = :memberId AND v.talkPick IS NOT NULL ORDER BY v.lastModifiedAt DESC")
    List<TalkPickVote> findAllByMemberIdAndTalkPickDesc(Long memberId);
}
