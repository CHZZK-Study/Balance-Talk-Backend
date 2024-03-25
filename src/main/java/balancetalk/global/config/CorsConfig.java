package balancetalk.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
//public class CorsConfig implements WebMvcConfigurer {
//
//    private final int MAX_AGE_SEC = 3600;
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:8080", "http://localhost:3000")
//                .exposedHeaders("Authorization")
//                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
//                .allowCredentials(true)
//                .maxAge(MAX_AGE_SEC);
//    }
//}