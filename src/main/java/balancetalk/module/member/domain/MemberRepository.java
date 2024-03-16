package balancetalk.module.member.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String username);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
    void deleteByEmail(String email);

    @Query("select m.id from Member m JOIN m.votes v WHERE v.balanceOption.id = :balanceOptionId")
    List<Long> findMemberIdsBySelectedOptionId(Long balanceOptionId);
}
