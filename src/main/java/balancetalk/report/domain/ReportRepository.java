package balancetalk.report.domain;

import balancetalk.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByReporterAndReportedAndResourceId(Member reporter, Member reported, Long commentId);
}
