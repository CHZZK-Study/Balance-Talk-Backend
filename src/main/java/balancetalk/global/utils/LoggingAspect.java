package balancetalk.global.utils;

import balancetalk.member.dto.ApiMember;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* *..*Controller.*(..))")
    public void pointcut() {} // pointcut signature

    @Around("pointcut()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();

        String userIp = request.getRemoteAddr();
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        Object[] args = joinPoint.getArgs();

        Long memberId = Arrays.stream(args)
                .filter(arg -> arg instanceof ApiMember)
                .map(arg -> (ApiMember) arg)
                .map(ApiMember::getMemberId)
                .findFirst()
                .orElse(null);
        int status = response.getStatus();
        if (memberId == null) {
            log.info("[REQUEST] {} GUEST {} {} args={}", userIp, method, requestURI, args);
        }
        else {
            log.info("[REQUEST] {} memberId={} {} {} args={}", userIp, memberId, method, requestURI, args);
        }

        try {
            Object proceed = joinPoint.proceed();
            log.info("[RESPONSE] {} {}", status, proceed);
            return proceed;
        } catch (Exception e) {
            log.error("[RESPONSE] {} {} {}", status, e.getMessage(), e.getStackTrace());
            throw e;
        }
    }
}
