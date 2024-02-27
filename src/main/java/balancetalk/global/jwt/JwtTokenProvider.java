package balancetalk.global.jwt;

import balancetalk.global.redis.application.RedisService;
import balancetalk.module.member.domain.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.time.Duration;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final RedisService redisService;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Value("${spring.jwt.token.access-expiration-time}")
    private long accessExpirationTime;

    @Value("${spring.jwt.token.refresh-expiration-time}")
    private long refreshExpirationTime;

    private final UserDetailsService userDetailsService;

    /**
     * Access 토큰 생성
     */
    public String createAccessToken(Authentication authentication) {
        Claims claims = Jwts.claims().setSubject(authentication.getName());
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + accessExpirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, secretKey) // TODO: secretKey를 미리 암호화?
                .compact();
    }

    /**
     * Refresh 토큰 생성
     */
    public String createRefreshToken(Authentication authentication) {
        Claims claims = Jwts.claims().setSubject(authentication.getName());
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + refreshExpirationTime);

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
        // redis에 refresh token 저장
        redisService.setValues(authentication.getName(), refreshToken, Duration.ofMillis(refreshExpirationTime));
        return refreshToken;
    }

    // 토큰으로부터 클레임을 만들고, User 객체를 생성해서 Authentication 객체 반환
    public Authentication getAuthentication(String token) {
        String userPrincipal = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPrincipal);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

    }

    // http 헤더로부터 bearer 토큰 가져옴
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.info("bearerToken={}", bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // 실제 토큰만 추출
        }
        return null;
     }

    public String getPayload(String token) {
        return tokenToJws(token).getBody().getSubject();
    }

    private Jws<Claims> tokenToJws(final String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
        } catch (final IllegalArgumentException | MalformedJwtException e) {
            throw new IllegalArgumentException("Token이 null이거나 Token 파싱 오류");
        } catch (final SignatureException e) {
            throw new IllegalArgumentException("토큰의 시크릿 키가 일치하지 않습니다.");
        } catch (final ExpiredJwtException e) {
            throw new IllegalArgumentException("만료된 토큰 입니다.");
        }
    }

    // 토큰 유효성, 만료일자 확인
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("토큰 만료");
        } catch (JwtException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("유효하지 않은 JWT");
        }
    }
}
