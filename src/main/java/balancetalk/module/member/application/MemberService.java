package balancetalk.module.member.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.global.jwt.JwtTokenProvider;
import balancetalk.global.redis.application.RedisService;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.member.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final RedisService redisService;

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

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);
        TokenDto tokenDto = jwtTokenProvider.reissueToken(refreshToken); // 만료되었다면, 재발급
        return LoginSuccessDto.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .role(member.getRole())
                .tokenDto(tokenDto)
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

    @Transactional
    public void updateNickname(final NicknameUpdate nicknameUpdate, HttpServletRequest request) {
        Member member = extractMember(request);
        member.updateNickname(nicknameUpdate.getNickname());
    }

    @Transactional
    public void updatePassword(final PasswordUpdate passwordUpdate, HttpServletRequest request) {
        Member member = extractMember(request);
        member.updatePassword(passwordEncoder.encode(passwordUpdate.getPassword()));
    }

    @Transactional
    public void delete(final LoginDto loginDto, HttpServletRequest request) {
        Member member = extractMember(request);
        if (!member.getEmail().equals(loginDto.getEmail())) {
            throw new BalanceTalkException(ErrorCode.FORBIDDEN_MEMBER_DELETE);
        }

        if (!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            throw new BalanceTalkException(ErrorCode.MISMATCHED_EMAIL_OR_PASSWORD);
        }
        memberRepository.deleteByEmail(member.getEmail());
    }

    @Transactional
    public void logout(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        if (redisService.getValues(email) == null) {
            throw new BalanceTalkException(ErrorCode.UNAUTHORIZED_LOGOUT);
        }
        redisService.deleteValues(email);
    }

    private Member extractMember(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        String email = jwtTokenProvider.getPayload(token);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_MEMBER));
        return member;
    }
}
