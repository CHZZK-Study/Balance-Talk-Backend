package balancetalk.module.report.domain;

import balancetalk.module.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByReporter(Member reporter);
}