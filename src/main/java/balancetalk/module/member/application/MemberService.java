package balancetalk.module.member.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.global.jwt.JwtTokenProvider;
import balancetalk.global.redis.application.RedisService;
import balancetalk.module.comment.domain.Comment;
import balancetalk.module.file.domain.File;
import balancetalk.module.file.domain.FileRepository;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.member.dto.*;
import balancetalk.module.post.domain.Post;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static balancetalk.global.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final FileRepository fileRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    @Transactional
    public Long join(final JoinRequest joinRequest) {
        if (memberRepository.existsByEmail(joinRequest.getEmail())) {
            throw new BalanceTalkException(ALREADY_REGISTERED_EMAIL);
        }
        if (memberRepository.existsByNickname(joinRequest.getNickname())) {
            throw new BalanceTalkException(ALREADY_REGISTERED_NICKNAME);
        }
        joinRequest.setPassword(passwordEncoder.encode(joinRequest.getPassword()));
        File profilePhoto = null;
        if (joinRequest.getProfilePhoto() != null && !joinRequest.getProfilePhoto().isEmpty()) {
            profilePhoto = fileRepository.findByStoredName(joinRequest.getProfilePhoto())
                    .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_FILE));
        }
        Member member = joinRequest.toEntity(profilePhoto);
        return memberRepository.save(member).getId();
    }


    @Transactional
    public String login(final LoginRequest loginRequest, HttpServletResponse response) {
        Member member = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.MISMATCHED_EMAIL_OR_PASSWORD));
        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new BalanceTalkException(ErrorCode.MISMATCHED_EMAIL_OR_PASSWORD);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        String accessToken = jwtTokenProvider.createAccessToken(authentication, member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);
        Cookie cookie = jwtTokenProvider.createCookie(refreshToken);
        response.addCookie(cookie);
        return accessToken;
    }

    @Transactional(readOnly = true)
    public MemberResponse findById(Long id) {
        Member member = memberRepository.findById(id)
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

    @Transactional
    public void updateNickname(final String newNickname, HttpServletRequest request) {
        Member member = extractMember(request);
        if (member.getNickname().equals(newNickname)) {
            throw new BalanceTalkException(ErrorCode.SAME_NICKNAME);
        }
        member.updateNickname(newNickname);
    }

    @Transactional
    public void updatePassword(final String newPassword, HttpServletRequest request) {
        Member member = extractMember(request);
        if (passwordEncoder.matches(newPassword, member.getPassword())){
            throw new BalanceTalkException(ErrorCode.SAME_PASSWORD);
        }
        member.updatePassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public void updateImage(String storedFileName, HttpServletRequest request) {
        Member member = extractMember(request);
        File file = fileRepository.findByStoredName(storedFileName)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_FILE));
        member.updateImage(file);
    }

    @Transactional
    public void delete(final LoginRequest loginRequest, HttpServletRequest request) {
        Member member = extractMember(request);
        if (!member.getEmail().equals(loginRequest.getEmail())) {
            throw new BalanceTalkException(ErrorCode.FORBIDDEN_MEMBER_DELETE);
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new BalanceTalkException(ErrorCode.MISMATCHED_EMAIL_OR_PASSWORD);
        }

        List<Post> posts = member.getPosts();
        if (posts != null) {
            for (Post post : posts) {
                post.removeMember();
            }
        }
        memberRepository.deleteByEmail(member.getEmail());
    }

    @Transactional
    public void logout(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new BalanceTalkException(ErrorCode.AUTHENTICATION_REQUIRED);
        }
        String username = authentication.getName();
        redisService.deleteValues(username);
    }

    public void verifyNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new BalanceTalkException(ErrorCode.ALREADY_REGISTERED_NICKNAME);
        }
    }

    private Member extractMember(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        String email = jwtTokenProvider.getPayload(token);
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_MEMBER));
    }

    public String reissueAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            if (name.equals("refreshToken")) {
                String refreshToken = cookie.getValue();
                jwtTokenProvider.validateToken(refreshToken);
                Long memberId = jwtTokenProvider.getMemberId(refreshToken);
                return jwtTokenProvider.reissueAccessToken(refreshToken, memberId);
            }
        }
        return null;
    }
}
