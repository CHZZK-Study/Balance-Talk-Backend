package balancetalk.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
@ConfigurationProperties(prefix = "spring.servlet.multipart")
@Getter
@Setter
public class FileConfig {
    private DataSize maxFileSize;
    private DataSize maxRequestSize;
    private String location;
}
