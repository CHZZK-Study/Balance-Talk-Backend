package balancetalk.global.oauth2;

import balancetalk.global.jwt.JwtTokenProvider;
import balancetalk.global.oauth2.dto.CustomOAuth2User;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import static balancetalk.global.jwt.JwtTokenProvider.createAccessCookie;
import static balancetalk.global.jwt.JwtTokenProvider.createCookie;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // Oauth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        Member member = memberRepository.findByUsername(username);
        String accessToken = jwtTokenProvider.createAccessToken(authentication, member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        response.addCookie(createCookie(refreshToken));
        response.addCookie(createAccessCookie(accessToken));
        response.sendRedirect("http://localhost:3000/");
    }

}
