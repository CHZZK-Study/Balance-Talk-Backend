package balancetalk.global.common;

import balancetalk.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.OK;

public class ApiResponse<T> {

    private static final String SUCCESS = "SUCCESS";

    private int code;
    private HttpStatus status;
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

    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return new ApiResponse<>(status, message);
    }
}