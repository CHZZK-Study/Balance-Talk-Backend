package balancetalk.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);

        // 토큰이 유효할 때
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰으로부터 유저 정보를 받는다
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            // SecurityContext에 객체 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // 다음 필터로 진행
        chain.doFilter(request, response);
    }
}
