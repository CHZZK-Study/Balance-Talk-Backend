package balancetalk.global.common;

import balancetalk.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.OK;

@Getter
public class ApiResponse<T> {

    private static final String SUCCESS = "SUCCESS";

    @Schema(description = "HTTP 상태 코드", example = "201")
    private int code;

    @Schema(description = "HTTP 상태", example = "CREATED")
    private HttpStatus status;

    @Schema(description = "HTTP 상태 메시지", example = "HTTP 상태 메시지")
    private String message;

    private T data;

    private ApiResponse(HttpStatus status, String message, T data) {
        this.code = status.value();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    private ApiResponse(HttpStatus status, String message) {
        this.code = status.value();
        this.status = status;
        this.message = message;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(OK, SUCCESS, data);
    }

    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(OK, SUCCESS, null);
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return new ApiResponse<>(status, message);
    }
}