package balancetalk.global.jwt;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.global.utils.AuthPrincipal;
import balancetalk.member.dto.ApiMember;
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
public class ApiMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(ApiMember.class) && parameter.hasParameterAnnotation(
                AuthPrincipal.class);
    }

    @Override
<<<<<<< HEAD:src/main/java/balancetalk/global/jwt/UserArgumentResolver.java
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest httpServletRequest = (HttpServletRequest) webRequest.getNativeRequest();

        String token = jwtTokenProvider.resolveToken(httpServletRequest);

        // 임시 추가
        if (token == null) {
            return null;
        }

=======
    public ApiMember resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String accessToken = jwtTokenProvider.resolveToken(request);

        if (accessToken == null) {
            throw new BalanceTalkException(ErrorCode.EMPTY_JWT_TOKEN);
        }

        String token = jwtTokenProvider.resolveToken(request);
>>>>>>> 82069c9883056247cf39fa48ba164beca46020ac:src/main/java/balancetalk/global/jwt/ApiMemberArgumentResolver.java
        jwtTokenProvider.validateToken(token);
        String email = jwtTokenProvider.getPayload(token);

        return new ApiMember(email);
    }
}
