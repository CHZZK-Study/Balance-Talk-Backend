package balancetalk.global.config;

import balancetalk.global.jwt.ApiMemberArgumentResolver;
import balancetalk.global.jwt.GuestOrApiMemberArgumentResolver;
import balancetalk.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new GuestOrApiMemberArgumentResolver(jwtTokenProvider));
        resolvers.add(new ApiMemberArgumentResolver(jwtTokenProvider));
    }
}
