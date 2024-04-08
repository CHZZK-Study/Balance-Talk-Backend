package balancetalk.global.jwt;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.global.redis.application.RedisService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    /**
     * Access 토큰 생성
     */
    public String createAccessToken(Authentication authentication, Long memberId) {
        validateAuthentication(authentication);
        Claims claims = Jwts.claims();
        claims.put("memberId", memberId);
        claims.setSubject(authentication.getName());
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
        validateAuthentication(authentication);
        Claims claims = Jwts.claims();
        claims.setSubject(authentication.getName());
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

    public Cookie createCookie(String refreshToken) {
        String cookieName = "refreshToken";
        Cookie cookie = new Cookie(cookieName, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24); // accessToken 유효
        return cookie;
    }

    // 토큰으로부터 클레임을 만들고, User 객체를 생성해서 Authentication 객체 반환
    public Authentication getAuthentication(String token) {
        String userPrincipal = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPrincipal);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getPayload(String token) {
        validateToken(token);
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new BalanceTalkException(ErrorCode.EXPIRED_JWT_TOKEN);
        } catch (IllegalArgumentException | MalformedJwtException e) {
            throw new BalanceTalkException(ErrorCode.EMPTY_JWT_TOKEN);
        } catch (SignatureException e) {
            throw new BalanceTalkException(ErrorCode.INVALID_JWT_TOKEN);
        }
    }

    private void validateAuthentication(Authentication authentication) {
        if (authentication == null) {
            throw new BalanceTalkException(ErrorCode.NOT_FOUND_MEMBER);
        }
    }


    public String reissueAccessToken(String refreshToken, Long memberId) {
        validateToken(refreshToken);
        Authentication authentication = getAuthentication(refreshToken);
        // redis에 저장된 RefreshToken 값을 가져옴
        String redisRefreshToken = redisService.getValues(authentication.getName());
        if (redisRefreshToken == null) {
            throw new BalanceTalkException(ErrorCode.UNAUTHORIZED_REISSUE_TOKEN);
        }

        if (!redisRefreshToken.equals(refreshToken)) {
            throw new BalanceTalkException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        return createAccessToken(authentication, memberId);
    }
}
