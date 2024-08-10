package balancetalk.talkpick.domain.repository;

import balancetalk.member.domain.Member;
import balancetalk.talkpick.domain.TempTalkPick;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TempTalkPickRepository extends JpaRepository<TempTalkPick, Long> {
    Optional<TempTalkPick> findByMember(Member member);
}
