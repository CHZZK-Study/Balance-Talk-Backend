package balancetalk.global.exception;

import balancetalk.global.common.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Set;
import java.util.stream.Collectors;

import static balancetalk.global.exception.ErrorCode.FILE_SIZE_EXCEEDED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BalanceTalkException.class)
    public ApiResponse<Object> handleBalanceTalkException(BalanceTalkException e) {
        log.error("exception message = {}", e.getMessage());
        return ApiResponse.error(e.getErrorCode().getHttpStatus(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        log.error("exception message = {}", message);
        return ApiResponse.error(BAD_REQUEST, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<Object> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("\n"));
        log.error("exception message = {}", message);
        return ApiResponse.error(BAD_REQUEST, message);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ApiResponse<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("exception message = {}", e.getMessage());
        return ApiResponse.error(FILE_SIZE_EXCEEDED.getHttpStatus(), e.getMessage());
    }
}
