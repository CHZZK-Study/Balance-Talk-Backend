package balancetalk.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestControllerAdvice
public class MethodArgumentException {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleNoticeTitleSizeExceededExceptions(MethodArgumentNotValidException e) {
        Map<Object, Object> errors = new HashMap<>();
        FieldError fieldError = e.getBindingResult().getFieldError();
        String errorMessage = fieldError.getDefaultMessage();
        errors.put("message" , errorMessage);
        log.info("errorMessage={}", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
//        ErrorResponse errorResponse = ErrorResponse.from(ErrorCode.EXCEED_VALIDATION_LENGTH);
//        log.error("exception message = {}", e.getMessage());
//        return ResponseEntity.status(errorResponse.getHttpStatus()).body(errorResponse);
    }
}