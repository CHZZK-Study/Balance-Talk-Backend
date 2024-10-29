package balancetalk.talkpick.application;

import balancetalk.file.application.FileService;
import balancetalk.file.domain.File;
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

import static balancetalk.file.domain.FileType.TEMP_TALK_PICK;
import static balancetalk.talkpick.dto.TempTalkPickDto.FindTempTalkPickResponse;
import static balancetalk.talkpick.dto.TempTalkPickDto.SaveTempTalkPickRequest;

@Service
@RequiredArgsConstructor
public class TempTalkPickService {

    private final MemberRepository memberRepository;
    private final TempTalkPickRepository tempTalkPickRepository;
    private final FileRepository fileRepository;
    private final FileService fileService;

    @Transactional
    public void createTempTalkPick(SaveTempTalkPickRequest request, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        List<File> files = fileRepository.findAllById(request.getFileIds());

        if (member.hasTempTalkPick()) {
            Long tempTalkPickId = member.updateTempTalkPick(request.toEntity(member));
            moveDirectory(files, tempTalkPickId);
            return;
        }

        TempTalkPick savedTempTalkPick = tempTalkPickRepository.save(request.toEntity(member));
        moveDirectory(files, savedTempTalkPick.getId());
    }

    private void moveDirectory(List<File> files, Long tempTalkPickId) {
        for (File file : files) {
            String destinationKey = getDestinationKey(tempTalkPickId, file);
            fileService.update(file, TEMP_TALK_PICK, destinationKey);
        }
    }

    private String getDestinationKey(Long tempTalkPickId, File file) {
        return "%s%d/%s".formatted(TEMP_TALK_PICK.getUploadDir(), tempTalkPickId, file.getStoredName());
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
