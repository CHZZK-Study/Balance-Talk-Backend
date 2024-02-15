package balancetalk.module.member.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.global.jwt.JwtTokenProvider;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.member.dto.JoinDto;
import balancetalk.module.member.dto.LoginDto;
import balancetalk.module.member.dto.LoginSuccessDto;
import balancetalk.module.member.dto.MemberResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
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
    public LoginSuccessDto login(final LoginDto loginDto) {
        Member member = memberRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.MISMATCHED_EMAIL_OR_PASSWORD));
        if (!member.getPassword().equals(loginDto.getPassword())) {
            throw new BalanceTalkException(ErrorCode.MISMATCHED_EMAIL_OR_PASSWORD);
        }
        String token = jwtTokenProvider.createToken(member.getEmail(), member.getRole());

        if (token == null) {
            throw new BalanceTalkException(ErrorCode.AUTHENTICATION_ERROR);
        }

        return LoginSuccessDto.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .role(member.getRole())
                .token(token)
                .build();
    }

    @Transactional(readOnly = true)
    public MemberResponseDto findById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_MEMBER));
        return MemberResponseDto.fromEntity(member);
    }

    @Transactional(readOnly = true)
    public List<MemberResponseDto> findAll() {
        List<Member> members = memberRepository.findAll();
        return members.stream()
                .map(MemberResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
