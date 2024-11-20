package balancetalk.talkpick.application;

import static balancetalk.file.domain.FileType.TEMP_TALK_PICK;
import static balancetalk.talkpick.dto.TempTalkPickDto.FindTempTalkPickResponse;
import static balancetalk.talkpick.dto.TempTalkPickDto.SaveTempTalkPickRequest;

import balancetalk.file.domain.File;
import balancetalk.file.domain.FileHandler;
import balancetalk.file.domain.repository.FileRepository;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.talkpick.domain.TempTalkPick;
import balancetalk.talkpick.domain.repository.TempTalkPickRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TempTalkPickService {

    private final MemberRepository memberRepository;
    private final TempTalkPickRepository tempTalkPickRepository;
    private final FileRepository fileRepository;
    private final FileHandler fileHandler;

    @Transactional
    public void createTempTalkPick(SaveTempTalkPickRequest request, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);

        if (member.hasTempTalkPick()) {
            Long tempTalkPickId = member.updateTempTalkPick(request.toEntity(member));
            relocateFilesIfContainsFileIds(request, tempTalkPickId);
            return;
        }

        TempTalkPick savedTempTalkPick = tempTalkPickRepository.save(request.toEntity(member));
        relocateFilesIfContainsFileIds(request, savedTempTalkPick.getId());
    }

    private void relocateFilesIfContainsFileIds(SaveTempTalkPickRequest request, Long tempTalkPickId) {
        if (request.notContainsAnyFileIds()) {
            return;
        }

        if (request.containsNewFileIds()) {
            List<Long> newFileIds = request.getNewFileIds();
            List<Long> deletedFileIds = deleteRequestedFiles(request);
            newFileIds.removeIf((deletedFileIds::contains));
            relocateFiles(newFileIds, tempTalkPickId);
            return;
        }

        if (request.containsDeleteFileIds()) {
            deleteRequestedFiles(request);
        }
    }

    private List<Long> deleteRequestedFiles(SaveTempTalkPickRequest request) {
        if (request.containsDeleteFileIds()) {
            List<Long> deleteFileIds = request.getDeleteFileIds();
            List<File> files = fileRepository.findAllById(deleteFileIds);
            fileHandler.deleteFiles(files);
            return deleteFileIds;
        } else {
            return List.of();
        }
    }

    private void relocateFiles(List<Long> fileIds, Long tempTalkPickId) {
        List<File> files = fileRepository.findAllById(fileIds);
        fileHandler.relocateFiles(files, tempTalkPickId, TEMP_TALK_PICK);
    }

    public FindTempTalkPickResponse findTempTalkPick(ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        TempTalkPick tempTalkPick = getTempTalkPick(member);

        return FindTempTalkPickResponse.from(tempTalkPick, getImgUrls(tempTalkPick), getFileIds(tempTalkPick));
    }

    private TempTalkPick getTempTalkPick(Member member) {
        return tempTalkPickRepository.findByMember(member)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_TEMP_TALK_PICK));
    }

    private List<String> getImgUrls(TempTalkPick tempTalkPick) {
        return fileRepository.findImgUrlsByResourceIdAndFileType(tempTalkPick.getId(), TEMP_TALK_PICK);
    }

    private List<Long> getFileIds(TempTalkPick tempTalkPick) {
        return fileRepository.findIdsByResourceIdAndFileType(tempTalkPick.getId(), TEMP_TALK_PICK);
    }
}
