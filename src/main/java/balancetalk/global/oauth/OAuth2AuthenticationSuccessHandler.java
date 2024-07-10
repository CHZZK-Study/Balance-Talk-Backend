package balancetalk.global.oauth;

import balancetalk.global.jwt.JwtTokenProvider;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.domain.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@AllArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = token.getPrincipal();
        String registrationId = token.getAuthorizedClientRegistrationId();

        // 사용자 정보 가져오기
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Optional<Member> existingMember = memberRepository.findByEmail(email);
        Member member;
        if (existingMember.isEmpty()) {
            member = Member.builder()
                    .email(email)
                    .nickname(name)
                    .password("")  // 소셜 로그인은 비밀번호가 필요 없음
                    .role(Role.USER)
                    .build();
            memberRepository.save(member);
        } else {
            member = existingMember.get();
        }

        // JWT 토큰 생성 및 반환
        Authentication auth = new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities());
        String accessToken = jwtTokenProvider.createAccessToken(auth, member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(auth);

        Cookie cookie = jwtTokenProvider.createCookie(refreshToken);
        response.addCookie(cookie);

        String redirectUrl = determineRedirectUrl(registrationId);
        response.sendRedirect(redirectUrl + "?token=" + accessToken);
    }

    private String determineRedirectUrl(String registrationId) {
        return switch (registrationId) {
            case "google" -> "http://localhost:3000/google-success";
            case "kakao" -> "http://localhost:3000/kakao-success";
            case "naver" -> "http://localhost:3000/naver-success";
            default -> throw new IllegalStateException("Unexpected value: " + registrationId);
        };
    }
}
