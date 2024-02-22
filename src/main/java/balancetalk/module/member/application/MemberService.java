package balancetalk.module.member.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.global.jwt.JwtTokenProvider;
import balancetalk.global.redis.application.RedisService;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.member.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long join(final JoinDto joinDto) {
        joinDto.setPassword(passwordEncoder.encode(joinDto.getPassword()));
        Member member = joinDto.toEntity();
        return memberRepository.save(member).getId();
    }

    @Transactional
    public LoginSuccessDto login(final LoginDto loginDto) {
        Member member = memberRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.MISMATCHED_EMAIL_OR_PASSWORD));
        if (!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            throw new BalanceTalkException(ErrorCode.MISMATCHED_EMAIL_OR_PASSWORD);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );

            return LoginSuccessDto.builder()
                    .email(member.getEmail())
                    .password(member.getPassword())
                    .role(member.getRole())
                    .accessToken(jwtTokenProvider.createAccessToken(authentication))
                    .refreshToken(jwtTokenProvider.createRefreshToken(authentication))
                    .build();
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("credential 오류!!");
        }
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

    @Transactional
    public MemberResponseDto update(Long memberId, final MemberUpdateDto memberUpdateDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_MEMBER));
        member.updateMember(memberUpdateDto.getNickname(), memberUpdateDto.getPassword());
        return MemberResponseDto.fromEntity(member);
    }

    @Transactional
    public void delete(Long memberId, final LoginDto loginDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_MEMBER));

        if (!member.getEmail().equals(loginDto.getEmail())) {
            throw new BalanceTalkException(ErrorCode.FORBIDDEN_MEMBER_DELETE);
        }
        if (!member.getPassword().equals(loginDto.getPassword())) {
            throw new BalanceTalkException(ErrorCode.INCORRECT_PASSWORD);
        }

        memberRepository.deleteById(memberId);
    }
}
