package balancetalk.module.vote.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByMemberIdAndBalanceOption_PostId(Long memberId, Long postId);
}
