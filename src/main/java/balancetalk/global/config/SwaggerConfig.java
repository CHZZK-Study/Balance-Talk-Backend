package balancetalk.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "Balance Talk API 명세서",
                version = "v1")
)

@Configuration
public class SwaggerConfig {

    // TODO: JWT 기능 추가
}
