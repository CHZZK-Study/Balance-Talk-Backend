package balancetalk.global.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class FileExceptionHandler {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        ErrorResponse errorResponse = ErrorResponse.from(ErrorCode.FILE_SIZE_EXCEEDED);
        log.error("exception message = {}", e.getMessage());
        return ResponseEntity.status(errorResponse.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        ErrorResponse errorResponse = ErrorResponse.from(ErrorCode.FILE_NAME_EXCEEDED);
        log.error("exception message = {}", e.getMessage());
        return ResponseEntity.status(errorResponse.getHttpStatus()).body(errorResponse);
    }
}
