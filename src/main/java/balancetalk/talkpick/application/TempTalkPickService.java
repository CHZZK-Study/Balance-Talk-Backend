package balancetalk.talkpick.application;

import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.talkpick.domain.TempTalkPick;
import balancetalk.talkpick.domain.repository.TempTalkPickRepository;
import balancetalk.talkpick.dto.TempTalkPickDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TempTalkPickService {

    private final MemberRepository memberRepository;
    private final TempTalkPickRepository tempTalkPickRepository;

    @Transactional
    public void createTempTalkPick(TempTalkPickDto.Request request, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        TempTalkPick tempTalkPick = request.toEntity(member);

        tempTalkPickRepository.findByMember(member)
                .ifPresentOrElse(
                        prevTempTalkPick -> prevTempTalkPick.update(tempTalkPick),
                        () -> tempTalkPickRepository.save(tempTalkPick));
    }
}
