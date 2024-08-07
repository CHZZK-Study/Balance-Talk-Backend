package balancetalk.member.application;

import balancetalk.global.caffeine.CacheType;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.global.jwt.JwtTokenProvider;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.member.dto.MemberDto.JoinRequest;
import balancetalk.member.dto.MemberDto.LoginRequest;
import balancetalk.member.dto.MemberDto.MemberResponse;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static balancetalk.global.exception.ErrorCode.ALREADY_REGISTERED_EMAIL;
import static balancetalk.global.exception.ErrorCode.ALREADY_REGISTERED_NICKNAME;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CacheManager cacheManager;

    public Long join(final JoinRequest joinRequest) {
        if (memberRepository.existsByEmail(joinRequest.getEmail())) {
            throw new BalanceTalkException(ALREADY_REGISTERED_EMAIL);
        }
        if (memberRepository.existsByNickname(joinRequest.getNickname())) {
            throw new BalanceTalkException(ALREADY_REGISTERED_NICKNAME);
        }
        joinRequest.setPassword(passwordEncoder.encode(joinRequest.getPassword()));
        Member member = joinRequest.toEntity();
        return memberRepository.save(member).getId();
    }

    public String login(final LoginRequest loginRequest, HttpServletResponse response) {
        Member member = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.MISMATCHED_EMAIL_OR_PASSWORD));
        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new BalanceTalkException(ErrorCode.MISMATCHED_EMAIL_OR_PASSWORD);
        }

        Authentication authentication = jwtTokenProvider.getAuthenticationByEmail(loginRequest.getEmail());
        String accessToken = jwtTokenProvider.createAccessToken(authentication, member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication, member.getId());

        cacheManager.getCache(CacheType.RefreshToken.getCacheName()).put(member.getId(), refreshToken);

        Cookie cookie = jwtTokenProvider.createCookie(refreshToken);
        response.addCookie(cookie);
        return accessToken;
    }

    @Transactional(readOnly = true)
    public MemberResponse findById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_MEMBER));
        return MemberResponse.fromEntity(member);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> findAll() {
        List<Member> members = memberRepository.findAll();
        return members.stream()
                .map(MemberResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public void updateNickname(final String newNickname, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        if (member.getNickname().equals(newNickname)) {
            throw new BalanceTalkException(ErrorCode.SAME_NICKNAME);
        }
        member.updateNickname(newNickname);
    }

    public void updatePassword(final String newPassword, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        if (passwordEncoder.matches(newPassword, member.getPassword())) {
            throw new BalanceTalkException(ErrorCode.SAME_PASSWORD);
        }
        member.updatePassword(passwordEncoder.encode(newPassword));
    }

    public void updateImage(final String profileImgUrl, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        member.updateImgUrl(profileImgUrl);
    }

    public void delete(final LoginRequest loginRequest, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        if (!member.getEmail().equals(loginRequest.getEmail())) {
            throw new BalanceTalkException(ErrorCode.FORBIDDEN_MEMBER_DELETE);
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new BalanceTalkException(ErrorCode.MISMATCHED_EMAIL_OR_PASSWORD);
        }
        memberRepository.deleteByEmail(member.getEmail());
    }

    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new BalanceTalkException(ErrorCode.AUTHENTICATION_REQUIRED);
        }
    }

    public void verifyNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new BalanceTalkException(ErrorCode.ALREADY_REGISTERED_NICKNAME);
        }
    }

    public String reissueAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            if (name.equals("refreshToken")) {
                String refreshToken = cookie.getValue();
                jwtTokenProvider.validateToken(refreshToken);
                return jwtTokenProvider.reissueAccessToken(refreshToken);
            }
        }
        return null;
    }
}
