package balancetalk.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String username);
    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);
    void deleteByEmail(String email);

    Member findByUsername(String username);

//    @Query("select m.id from Member m JOIN m.votes v WHERE v.balanceOption.id = :balanceOptionId")
//    List<Long> findMemberIdsBySelectedOptionId(Long balanceOptionId);
}
