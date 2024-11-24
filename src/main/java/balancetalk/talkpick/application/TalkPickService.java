package balancetalk.talkpick.application;

import static balancetalk.file.domain.FileType.TALK_PICK;
import static balancetalk.global.exception.ErrorCode.NOT_FOUND_TALK_PICK;
import static balancetalk.talkpick.dto.TalkPickDto.CreateTalkPickRequest;
import static balancetalk.talkpick.dto.TalkPickDto.TalkPickDetailResponse;
import static balancetalk.talkpick.dto.TalkPickDto.TalkPickResponse;
import static balancetalk.talkpick.dto.TalkPickDto.UpdateTalkPickRequest;

import balancetalk.file.domain.File;
import balancetalk.file.domain.FileHandler;
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
    private final FileHandler fileHandler;

    @Transactional
    public Long createTalkPick(CreateTalkPickRequest request, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        TalkPick savedTalkPick = talkPickRepository.save(request.toEntity(member));
        Long savedTalkPickId = savedTalkPick.getId();
        if (request.containsFileIds()) {
            relocateFiles(request.getFileIds(), savedTalkPickId);
        }
        return savedTalkPickId;
    }

    private void relocateFiles(List<Long> fileIds, Long talkPickId) {
        List<File> files = fileRepository.findAllById(fileIds);
        fileHandler.relocateFiles(files, talkPickId, TALK_PICK);
    }

    @Transactional
    public TalkPickDetailResponse findById(Long talkPickId, GuestOrApiMember guestOrApiMember) {
        TalkPick talkPick = talkPickRepository.findById(talkPickId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_TALK_PICK));
        talkPick.increaseViews();

        List<String> imgUrls = fileRepository.findImgUrlsByResourceIdAndFileType(talkPickId, TALK_PICK);
        List<Long> fileIds = fileRepository.findIdsByResourceIdAndFileType(talkPickId, TALK_PICK);

        if (guestOrApiMember.isGuest()) {
            return TalkPickDetailResponse.from(talkPick, imgUrls, fileIds, false, null);
        }

        Member member = guestOrApiMember.toMember(memberRepository);
        boolean hasBookmarked = member.hasBookmarked(talkPick);
        Optional<TalkPickVote> myVote = member.getVoteOnTalkPick(talkPick);

        if (myVote.isEmpty()) {
            return TalkPickDetailResponse.from(talkPick, imgUrls, fileIds, hasBookmarked, null);
        }

        return TalkPickDetailResponse.from(talkPick, imgUrls, fileIds, hasBookmarked, myVote.get().getVoteOption());
    }

    public Page<TalkPickResponse> findPaged(Pageable pageable) {
        return talkPickRepository.findPagedTalkPicks(pageable);
    }

    public List<TalkPickResponse> findBestTalkPicks() {
        return talkPickRepository.findBestTalkPicks();
    }

    @Transactional
    public void updateTalkPick(Long talkPickId, UpdateTalkPickRequest request, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        TalkPick talkPick = member.getTalkPickById(talkPickId);
        talkPick.update(request.toEntity(member));

        if (request.notContainsAnyFileIds()) {
            return;
        }

        List<Long> deletedFileIds = deleteRequestedFiles(request);

        if (request.containsNewFileIds()) {
            List<Long> newFileIds = request.getNewFileIds();
            newFileIds.removeIf((deletedFileIds::contains));
            relocateFiles(newFileIds, talkPickId);
        }
    }

    private List<Long> deleteRequestedFiles(UpdateTalkPickRequest request) {
        if (request.containsDeleteFileIds()) {
            List<Long> deleteFileIds = request.getDeleteFileIds();
            List<File> files = fileRepository.findAllById(deleteFileIds);
            fileHandler.deleteFiles(files);
            return deleteFileIds;
        } else {
            return List.of();
        }
    }

    @Transactional
    public void deleteTalkPick(Long talkPickId, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        TalkPick talkPick = member.getTalkPickById(talkPickId);
        talkPickRepository.delete(talkPick);
        deleteAllAssociatedFiles(talkPickId);
    }

    private void deleteAllAssociatedFiles(Long talkPickId) {
        List<File> files = fileRepository.findAllByResourceIdAndFileType(talkPickId, TALK_PICK);
        if (files.isEmpty()) {
            return;
        }
        fileHandler.deleteFiles(files);
    }
}
