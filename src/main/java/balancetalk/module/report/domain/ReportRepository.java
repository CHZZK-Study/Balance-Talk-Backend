package balancetalk.module.report.domain;

import balancetalk.module.comment.domain.Comment;
import balancetalk.module.member.domain.Member;
import balancetalk.module.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByReporterAndPost(Member reporter, Post post);

    boolean existsByReporterAndComment(Member reporter, Comment comment);
}