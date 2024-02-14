package balancetalk.module.member.application;

import balancetalk.global.jwt.JwtTokenProvider;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.member.dto.JoinDto;
import balancetalk.module.member.dto.LoginDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Transactional
    public Long join(final JoinDto joinDto) {
        Member member = joinDto.toEntity();
        return memberRepository.save(member).getId();
    }

    @Transactional
    public String login(final LoginDto loginDto) {
        Member member = memberRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 사용자 입니다."));
        // TODO: Error Response 추가
        return jwtTokenProvider.createToken(member.getEmail(), member.getRole());
    }
}
