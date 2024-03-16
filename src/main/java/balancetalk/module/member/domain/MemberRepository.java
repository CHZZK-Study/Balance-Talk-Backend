package balancetalk.module.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String username);
    boolean existsByNickname(String nickname);
    boolean existsByEmail (String email);
    void deleteByEmail(String email);
}
