package balancetalk.module.member.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.global.jwt.JwtTokenProvider;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.member.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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
            TokenDto tokenDto = new TokenDto("Bearer", jwtTokenProvider.createAccessToken(authentication), jwtTokenProvider.createRefreshToken(authentication));
            return LoginSuccessDto.builder()
                    .email(member.getEmail())
                    .password(member.getPassword())
                    .role(member.getRole())
                    .tokenDto(tokenDto)
                    .build();
        } catch (BadCredentialsException e) {
            throw new BalanceTalkException(ErrorCode.BAD_CREDENTIAL_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public MemberResponseDto findById(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        String email = jwtTokenProvider.getPayload(token);
        Member member = memberRepository.findByEmail(email)
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
    public void updateNickname(Long memberId, final NicknameUpdate nicknameUpdate) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_MEMBER));
        member.updateNickname(nicknameUpdate.getNickname());
    }

    @Transactional
    public void updatePassword(Long memberId, final PasswordUpdate passwordUpdate) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_MEMBER));
        member.updatePassword(passwordUpdate.getPassword());
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
