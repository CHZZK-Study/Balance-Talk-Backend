package balancetalk.global.jwt;

import balancetalk.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonMessage = new HashMap<>();

        jsonMessage.put("httpStatus", "UNAUTHORIZED");
        jsonMessage.put("message", ErrorCode.AUTHENTICATION_REQUIRED.getMessage());
        String result = objectMapper.writeValueAsString(jsonMessage);

        response.getWriter().write(result);
    }
}
