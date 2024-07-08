package balancetalk.global.jwt;

import balancetalk.global.utils.AuthPrincipal;
import balancetalk.member.dto.MemberDto.TokenDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(TokenDto.class) && parameter.hasParameterAnnotation(
                AuthPrincipal.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) webRequest.getNativeRequest();

        if (httpServletRequest == null) {
            throw new BadRequestException();
        }

        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        jwtTokenProvider.validateToken(token);
        String email = jwtTokenProvider.getPayload(token);
        return new TokenDto(email);
    }
}
