package balancetalk.module.member.application;

import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.member.dto.JoinDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Long join(final JoinDto joinDto) {
        Member member = joinDto.toEntity();
        return memberRepository.save(member).getId();
    }
}
