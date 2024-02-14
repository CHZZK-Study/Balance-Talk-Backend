package balancetalk.global.exception;

import lombok.Getter;

@Getter
public class BalanceTalkException extends RuntimeException {

    private final ErrorCode errorCode;

    public BalanceTalkException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BalanceTalkException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BalanceTalkException(Throwable cause, ErrorCode errorCode) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}