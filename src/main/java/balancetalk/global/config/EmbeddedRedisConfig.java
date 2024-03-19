package balancetalk.global.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;
import java.io.IOException;

@Slf4j
@Profile("local")
@Configuration
public class EmbeddedRedisConfig {

    @Value("${spring.data.redis.port}")
    private int redisPort;

    private static final String REDIS_MAX_MEMORY = "maxmemory 128M";

    private RedisServer redisServer;

    @PostConstruct
    public void redisServer() throws IOException {
        try {
            redisServer = RedisServer.builder()
                    .port(redisPort)
                    .setting(REDIS_MAX_MEMORY)
                    .build();
            redisServer.start();
        } catch (Exception e) {
        }
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}
