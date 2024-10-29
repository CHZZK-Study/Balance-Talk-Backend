package balancetalk.talkpick.application;

import static balancetalk.file.domain.FileType.TALK_PICK;
import static balancetalk.global.exception.ErrorCode.NOT_FOUND_TALK_PICK;
import static balancetalk.talkpick.dto.TalkPickDto.CreateOrUpdateTalkPickRequest;
import static balancetalk.talkpick.dto.TalkPickDto.TalkPickDetailResponse;
import static balancetalk.talkpick.dto.TalkPickDto.TalkPickResponse;

import balancetalk.file.application.FileService;
import balancetalk.file.domain.File;
import balancetalk.file.domain.repository.FileRepository;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.member.dto.GuestOrApiMember;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.repository.TalkPickRepository;
import balancetalk.vote.domain.TalkPickVote;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TalkPickService {

    private final MemberRepository memberRepository;
    private final TalkPickRepository talkPickRepository;
    private final FileRepository fileRepository;
    private final FileService fileService;

    @Transactional
    public Long createTalkPick(CreateOrUpdateTalkPickRequest request, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        TalkPick savedTalkPick = talkPickRepository.save(request.toEntity(member));

        List<File> files = fileRepository.findAllById(request.getFileIds());
        for (File file : files) {
            String destinationKey = getDestinationKey(savedTalkPick.getId(), file);
            fileService.update(file, TALK_PICK, destinationKey);
        }

        return savedTalkPick.getId();
    }

    private String getDestinationKey(Long talkPickId, File file) {
        return "%s%d/%s".formatted(TALK_PICK.getUploadDir(), talkPickId, file.getStoredName());
    }

    @Transactional
    public TalkPickDetailResponse findById(Long talkPickId, GuestOrApiMember guestOrApiMember) {
        TalkPick talkPick = talkPickRepository.findById(talkPickId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_TALK_PICK));
        talkPick.increaseViews();

        List<String> imgUrls = fileRepository.findImgUrlsByResourceIdAndFileType(talkPickId, TALK_PICK);
        List<String> imgStoredNames = fileRepository.findStoredNamesByResourceIdAndFileType(talkPickId, TALK_PICK);

        if (guestOrApiMember.isGuest()) {
            return TalkPickDetailResponse.from(talkPick, imgUrls, imgStoredNames, false, null);
        }

        Member member = guestOrApiMember.toMember(memberRepository);
        boolean hasBookmarked = member.hasBookmarked(talkPick);
        Optional<TalkPickVote> myVote = member.getVoteOnTalkPick(talkPick);

        if (myVote.isEmpty()) {
            return TalkPickDetailResponse.from(talkPick, imgUrls, imgStoredNames, hasBookmarked, null);
        }

        return TalkPickDetailResponse.from(talkPick, imgUrls, imgStoredNames, hasBookmarked, myVote.get().getVoteOption());
    }

    public Page<TalkPickResponse> findPaged(Pageable pageable) {
        return talkPickRepository.findPagedTalkPicks(pageable);
    }

    public List<TalkPickResponse> findBestTalkPicks() {
        return talkPickRepository.findBestTalkPicks();
    }

    @Transactional
    public void updateTalkPick(Long talkPickId, CreateOrUpdateTalkPickRequest request, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        TalkPick talkPick = member.getTalkPickById(talkPickId);
        talkPick.update(request.toEntity(member));

        List<File> files = fileRepository.findAllById(request.getFileIds());
        for (File file : files) {
            String destinationKey = getDestinationKey(talkPick.getId(), file);
            fileService.update(file, TALK_PICK, destinationKey);
        }
    }

    @Transactional
    public void deleteTalkPick(Long talkPickId, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        TalkPick talkPick = member.getTalkPickById(talkPickId);
        talkPickRepository.delete(talkPick);
    }
}
