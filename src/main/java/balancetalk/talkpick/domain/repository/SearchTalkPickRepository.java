package balancetalk.talkpick.domain.repository;

import balancetalk.talkpick.domain.TalkPick;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchTalkPickRepository extends JpaRepository<TalkPick, Long>, SearchTalkPickRepositoryCustom {
}
