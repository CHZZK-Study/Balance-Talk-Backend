package balancetalk.talkpick.domain.repository;

import balancetalk.talkpick.domain.TalkPick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TalkPickRepository extends JpaRepository<TalkPick, Long>, TalkPickRepositoryCustom {

    @Query("SELECT t FROM TalkPick t WHERE t.id IN :ids ORDER BY t.createdAt DESC")
    List<TalkPick> findByIdInOrderByCreatedAtDesc(@Param("ids") List<Long> ids);
}
