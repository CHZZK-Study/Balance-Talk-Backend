package balancetalk.talkpick.application;

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

import static balancetalk.talkpick.dto.TempTalkPickDto.FindTempTalkPickResponse;
import static balancetalk.talkpick.dto.TempTalkPickDto.SaveTempTalkPickRequest;

@Service
@RequiredArgsConstructor
public class TempTalkPickService {

    private final MemberRepository memberRepository;
    private final TempTalkPickRepository tempTalkPickRepository;

    @Transactional
    public void createTempTalkPick(SaveTempTalkPickRequest request, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        TempTalkPick tempTalkPick = request.toEntity(member);

        tempTalkPickRepository.findByMember(member)
                .ifPresentOrElse(
                        prevTempTalkPick -> prevTempTalkPick.update(tempTalkPick),
                        () -> tempTalkPickRepository.save(tempTalkPick));
    }

    public FindTempTalkPickResponse findTempTalkPick(ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        TempTalkPick tempTalkPick = tempTalkPickRepository.findByMember(member)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_TEMP_TALK_PICK));

        return FindTempTalkPickResponse.from(tempTalkPick);
    }
}
