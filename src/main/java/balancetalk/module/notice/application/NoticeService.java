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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static balancetalk.global.exception.ErrorCode.*;
import static balancetalk.global.utils.SecurityUtils.getCurrentMember;
import static balancetalk.module.member.domain.Role.ADMIN;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public NoticeResponse createNotice(final NoticeRequest request) {
        Member member = getCurrentMember(memberRepository);
        if (!member.getRole().equals(ADMIN)) {
            throw new BalanceTalkException(FORBIDDEN_CREATE_NOTICE);
        }

        Notice notice = request.toEntity(member);

        return NoticeResponse.fromEntity(noticeRepository.save(notice));
    }

    @Transactional(readOnly = true)
    public Page<NoticeResponse> findAllNotices(Pageable pageable) {
        return noticeRepository.findAll(pageable)
                .map(NoticeResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public NoticeResponse findNoticeById(Long id) {
        return NoticeResponse.fromEntity(noticeRepository.findById(id)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_NOTICE)));
    }

    @Transactional
    public NoticeResponse updateNotice(Long id, NoticeRequest request) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_NOTICE));
        if (!getCurrentMember(memberRepository).getRole().equals(ADMIN)) {
            throw new BalanceTalkException(FORBIDDEN_UPDATE_NOTICE);
        }
        notice.updateTitle(request.getTitle());
        notice.updateContent(request.getContent());
        return NoticeResponse.fromEntity(noticeRepository.save(notice));
    }

    @Transactional
    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_NOTICE));
        if (!getCurrentMember(memberRepository).getRole().equals(ADMIN)) {
            throw new BalanceTalkException(FORBIDDEN_DELETE_NOTICE);
        }
        noticeRepository.delete(notice);
    }
}
