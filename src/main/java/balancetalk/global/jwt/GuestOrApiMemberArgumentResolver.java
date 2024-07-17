package balancetalk.global.jwt;

import balancetalk.global.utils.AuthPrincipal;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.GuestOrApiMember;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
@RequiredArgsConstructor
public class GuestOrApiMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(GuestOrApiMember.class) && parameter.hasParameterAnnotation(
                AuthPrincipal.class);
    }

    @Override
    public GuestOrApiMember resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String accessToken = jwtTokenProvider.resolveToken(request);
        log.info("accessToken={}", accessToken);

        if (accessToken == null) { // 비회원일 때
            return new GuestOrApiMember("guest");
        }

        String token = jwtTokenProvider.resolveToken(request);
        jwtTokenProvider.validateToken(token);
        String email = jwtTokenProvider.getPayload(token);

        return new GuestOrApiMember(email);
    }
}
