package balancetalk.report.application;

import balancetalk.comment.domain.Comment;
import balancetalk.comment.domain.CommentRepository;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.report.domain.Report;
import balancetalk.report.domain.ReportRepository;
import balancetalk.report.dto.ReportDto.CreateReportRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static balancetalk.global.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportCommentService {

    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createCommentReport(@Valid CreateReportRequest createReportRequest, ApiMember apiMember,
                                    Long resourceId, Long talkPickId) {

        Member reporter = apiMember.toMember(memberRepository);

        // 댓글과 톡픽 검증
        Comment comment = validateCommentOnTalkPick(resourceId, talkPickId);
        Member reported = comment.getMember();

        // 본인의 댓글을 신고할 수 없음 예외 처리
        if (reporter.equals(reported)) {
            throw new BalanceTalkException(REPORT_MY_COMMENT);
        }

        // 중복 신고 방지
        if (reportRepository.existsByReporterAndReportedAndResourceId(reporter, reported, resourceId)) {
            throw new BalanceTalkException(ALREADY_REPORTED_COMMENT);
        }

        Report report = createReportRequest.toEntity(reporter, reported, resourceId, comment.getContent());

        reportRepository.save(report);
        comment.incrementReportCount();
    }

    private Comment validateCommentOnTalkPick(Long resourceId, Long talkPickId) {
        Comment comment = commentRepository.findById(resourceId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_COMMENT));

        if (!comment.getTalkPick().getId().equals(talkPickId)) {
            throw new BalanceTalkException(NOT_FOUND_COMMENT_AT_THAT_TALK_PICK);
        }

        return comment;
    }
}
