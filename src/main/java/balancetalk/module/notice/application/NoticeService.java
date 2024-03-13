package balancetalk.module.notice.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.module.file.domain.File;
import balancetalk.module.file.domain.FileRepository;
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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static balancetalk.global.exception.ErrorCode.*;
import static balancetalk.global.utils.SecurityUtils.getCurrentMember;
import static balancetalk.module.member.domain.Role.ADMIN;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;
    private final FileRepository fileRepository;

    @Transactional
    public NoticeResponse createNotice(final NoticeRequest request) {
        Member member = getCurrentMember(memberRepository);
        if (!member.getRole().equals(ADMIN)) {
            throw new BalanceTalkException(FORBIDDEN_CREATE_NOTICE);
        }

        Notice notice = noticeRepository.save(request.toEntity(member));

        List<File> files = getFiles(request.getStoredFileNames(), notice);
        fileRepository.saveAll(files);

        List<String> storedFileNames = files.stream()
                .map(File::getStoredName)
                .toList();

        return NoticeResponse.fromEntity(notice, storedFileNames);
    }

    @Transactional(readOnly = true)
    public Page<NoticeResponse> findAllNotices(Pageable pageable) {
        return noticeRepository.findAll(pageable)
                .map(notice -> {
                    List<String> storedFileNames = fileRepository.findByNoticeId(notice.getId()).stream()
                            .map(File::getStoredName)
                            .collect(Collectors.toList());
                    return NoticeResponse.fromEntity(notice, storedFileNames);
                });
    }


    @Transactional(readOnly = true)
    public NoticeResponse findNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_NOTICE));
        List<String> storedFileNames = fileRepository.findByNoticeId(notice.getId()).stream()
                .map(File::getStoredName)
                .collect(Collectors.toList());
        return NoticeResponse.fromEntity(notice, storedFileNames);
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

        // 업데이트된 공지사항에 대한 파일 목록도 조회
        List<String> storedFileNames = fileRepository.findByNoticeId(notice.getId()).stream()
                .map(File::getStoredName)
                .collect(Collectors.toList());

        return NoticeResponse.fromEntity(noticeRepository.save(notice), storedFileNames);
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

    private List<File> getFiles(List<String> storedFileNames, Notice notice) {
        if (storedFileNames == null) return Collections.emptyList();

        return storedFileNames.stream()
                .filter(fileName -> fileName != null && !fileName.isEmpty())
                .map(fileName -> {
                    File file = fileRepository.findByStoredName(fileName)
                            .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_FILE));
                    file.setNotice(notice);
                    return file;
                })
                .toList();
    }
}
