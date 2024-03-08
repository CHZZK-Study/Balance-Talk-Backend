package balancetalk.module.notice.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.notice.domain.Notice;
import balancetalk.module.notice.domain.NoticeRepository;
import balancetalk.module.notice.dto.NoticeRequest;
import balancetalk.module.notice.dto.NoticeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static balancetalk.global.exception.ErrorCode.UNAUTHORIZED_CREATE_NOTICE;
import static balancetalk.global.utils.SecurityUtils.getCurrentMember;
import static balancetalk.module.member.domain.Role.ADMIN;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;

    public NoticeResponse createNotice(final NoticeRequest request) {
        Member member = getCurrentMember(memberRepository);
        if (!member.getRole().equals(ADMIN)) {
            throw new BalanceTalkException(UNAUTHORIZED_CREATE_NOTICE);
        }

        Notice notice = request.toEntity(member);

        return NoticeResponse.fromEntity(noticeRepository.save(notice));
    }
}
