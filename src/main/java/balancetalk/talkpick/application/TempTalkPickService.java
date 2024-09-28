package balancetalk.talkpick.application;

import balancetalk.file.domain.repository.FileRepository;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.talkpick.domain.TempTalkPick;
import balancetalk.talkpick.domain.repository.TempTalkPickRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static balancetalk.file.domain.FileType.TEMP_TALK_PICK;
import static balancetalk.talkpick.dto.TempTalkPickDto.FindTempTalkPickResponse;
import static balancetalk.talkpick.dto.TempTalkPickDto.SaveTempTalkPickRequest;

@Service
@RequiredArgsConstructor
public class TempTalkPickService {

    private final MemberRepository memberRepository;
    private final TempTalkPickRepository tempTalkPickRepository;
    private final FileRepository fileRepository;

    @Transactional
    public void createTempTalkPick(SaveTempTalkPickRequest request, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);

        if (member.hasTempTalkPick()) {
            Long tempTalkPickId = member.updateTempTalkPick(request.toEntity(member));
            updateFileResourceIdByStoredNames(tempTalkPickId, request.getStoredNames());
            return;
        }

        TempTalkPick savedTempTalkPick = tempTalkPickRepository.save(request.toEntity(member));
        updateFileResourceIdByStoredNames(savedTempTalkPick.getId(), request.getStoredNames());
    }

    private void updateFileResourceIdByStoredNames(Long resourceId, List<String> storedNames) {
        fileRepository.updateResourceIdAndTypeByStoredNames(resourceId, TEMP_TALK_PICK, storedNames);
    }

    public FindTempTalkPickResponse findTempTalkPick(ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        TempTalkPick tempTalkPick = getTempTalkPick(member);

        return FindTempTalkPickResponse.from(tempTalkPick, getImgUrls(tempTalkPick), getStoredNames(tempTalkPick));
    }

    private TempTalkPick getTempTalkPick(Member member) {
        return tempTalkPickRepository.findByMember(member)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_TEMP_TALK_PICK));
    }

    private List<String> getImgUrls(TempTalkPick tempTalkPick) {
        return fileRepository.findImgUrlsByResourceIdAndFileType(tempTalkPick.getId(), TEMP_TALK_PICK);
    }

    private List<String> getStoredNames(TempTalkPick tempTalkPick) {
        return fileRepository.findStoredNamesByResourceIdAndFileType(tempTalkPick.getId(), TEMP_TALK_PICK);
    }
}
