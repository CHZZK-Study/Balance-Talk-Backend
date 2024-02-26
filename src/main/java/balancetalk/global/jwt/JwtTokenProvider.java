package balancetalk.global.jwt;

import balancetalk.global.redis.application.RedisService;
import balancetalk.module.member.dto.TokenDto;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
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
    private final AuthenticationManager authenticationManager;
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
        if (bearerToken != null && bearerToken.startsWith("bearer ")) {
            return bearerToken.substring(7); // 실제 토큰만 추출
        }
        return null;
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
    public TokenDto reissueToken(String refreshToken) {
        validateToken(refreshToken);
        Authentication authentication = getAuthentication(refreshToken);

        // redis에 저장된 RefreshToken 값을 가져옴
        String redisRefreshToken = redisService.getValues(authentication.getName());
        if (!redisRefreshToken.equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token이 존재하지 않음.");
        }

        TokenDto tokenDto = new TokenDto(
                createAccessToken(authentication),
                createRefreshToken(authentication)
        );
        return tokenDto;
    }
}
