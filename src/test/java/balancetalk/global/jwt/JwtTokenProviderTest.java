package balancetalk.global.jwt;

import balancetalk.global.redis.application.RedisService;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.member.domain.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.neptunedata.model.IllegalArgumentException;

import java.util.Date;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static org.assertj.core.api.Assertions.*;
@SpringBootTest
@Transactional
class JwtTokenProviderTest {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @MockBean
    private RedisService redisService; // RedisService를 mock으로 대체

    @BeforeEach
    void setMember() {
        member = Member.builder()
                .id(1L)
                .email("testMember@gmail.com")
                .nickname("testMember")
                .password("TestMember!")
                .role(Role.USER)
                .build();
        memberRepository.save(member);
    }

    @Test
    @DisplayName("토큰이 올바르게 생성된다.")
    void createTokenSuccess() {
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword());

        // when
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // then
        assertThat(accessToken).isNotNull();
        assertThat(refreshToken).isNotNull();
        assertThat(accessToken).isNotEqualTo(refreshToken);
    }

    @Test
    @DisplayName("토큰 생성에 실패하는 경우 예외 처리")
    void createTokenFail() {
        assertThatThrownBy(() -> jwtTokenProvider.createAccessToken(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유저 정보가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("올바른 Authentication 정보로 payload를 조회한다")
    void getPayloadByValidToken(){
        // given
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword());
        String accessToken = jwtTokenProvider.createAccessToken(authentication);

        // when
        String payload = jwtTokenProvider.getPayload(accessToken);

        // then
        assertThat(payload).isEqualTo(authentication.getName());
    }

    @Test
    @DisplayName("올바르지 않은 Authentication 정보를 조회하는 경우 예외 처리")
    void getPayloadByInvalidToken() {
        assertThatThrownBy(() -> jwtTokenProvider.getPayload(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token이 null이거나 Token 파싱 오류");
    }

    @Test
    @DisplayName("secretKey의 길이가 512bit 보다 짧을 때 예외 처리")
    void invalidSecretKey() {
        assertThatThrownBy(() -> Jwts.builder()
                .signWith(HS512, "fakeKey")
                .compact())
                .isInstanceOf(WeakKeyException.class);
    }

    @Test
    @DisplayName("만료된 토큰으로 payload 조회 시 예외 처리")
    void getPayloadByExpiredToken() {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() - 80000);
        String expiredToken = Jwts.builder()
                .signWith(HS512, secretKey)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .compact();
        assertThatThrownBy(() -> jwtTokenProvider.getPayload(expiredToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("만료된 토큰 입니다.");

    }
}