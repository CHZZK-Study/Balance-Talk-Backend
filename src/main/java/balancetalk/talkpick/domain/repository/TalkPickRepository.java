package balancetalk.talkpick.domain.repository;

import balancetalk.talkpick.domain.TalkPick;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TalkPickRepository extends JpaRepository<TalkPick, Long>, TalkPickRepositoryCustom {

    List<TalkPick> findAllByMemberIdOrderByEditedAtDesc(Long memberId);
}
