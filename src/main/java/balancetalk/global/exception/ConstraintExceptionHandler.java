package balancetalk.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class ConstraintExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleNoticeTitleSizeExceededExceptions(MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = ErrorResponse.from(ErrorCode.EXCEED_VALIDATION_LENGTH);
        log.error("exception message = {}", e.getMessage());

        return ResponseEntity.status(errorResponse.getHttpStatus()).body(errorResponse);
    }
}