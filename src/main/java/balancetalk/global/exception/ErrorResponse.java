package balancetalk.global.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    private HttpStatus httpStatus;
    private String message;

    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getHttpStatus(), errorCode.getMessage());
    }

    // 필요하다면 메시지도 함께 전달하는 생성자 추가 (네이밍은 상의)
}