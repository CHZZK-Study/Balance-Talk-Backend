package balancetalk.global.redis.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.Duration;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;

@SpringBootTest
class RedisCrudTEST {

    final String KEY = "key";
    final String VALUE = "value";
    final Duration DURATION = Duration.ofMillis(5000);

    @Autowired
    private RedisService redisService;

    @BeforeEach
    void setUp() {
        redisService.setValues(KEY, VALUE, DURATION);
    }
    @Test
    @DisplayName("Redis 데이터 저장후 조회 - 성공")
    void Save_Find_Success() {
        // when
        String findValue = redisService.getValues(KEY);

        // then
        assertThat(findValue).isEqualTo(VALUE);
    }

    @Test
    @DisplayName("Redis에 저장된 데이터 수정 - 성공")
    void Update_Success() {
        // given
        String updatedValue = "updatedValue";
        redisService.setValues(KEY, updatedValue, DURATION);

        // when
        String findValue = redisService.getValues(KEY);

        // then
        assertThat(findValue).isEqualTo(updatedValue);
        assertThat(findValue).isNotEqualTo(VALUE);
    }

    @Test
    @DisplayName("Redis에 저장된 데이터 삭제 - 성공")
    void Delete_Success() {
        // when
        redisService.deleteValues(KEY);

        // then
        assertThat(redisService.getValues(KEY)).isEqualTo(null);
    }

    @Test
    @DisplayName("만료시간이 지간 데이터 삭제 - 성공")
    void Expired_Success() {
        // when
        String findValue = redisService.getValues(KEY);

        // then
        await().pollDelay(Duration.ofMillis(6000)).untilAsserted(
                () -> {
                    String expiredValue = redisService.getValues(KEY);
                    assertThat(expiredValue).isNotEqualTo(findValue);
                    assertThat(expiredValue).isEqualTo(null);
                }
        );
    }

}