package balancetalk.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BalanceTalkException.class) //
    public ResponseEntity<ErrorResponse> handle___Exception(BalanceTalkException e) {
        ErrorResponse errorResponse = ErrorResponse.from(e.getErrorCode());
        log.info("exception = {}", e.getMessage());
        return ResponseEntity.status(errorResponse.getHttpStatus()).body(errorResponse);
    }

    // ...
}