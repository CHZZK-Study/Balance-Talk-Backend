package balancetalk.global.exception;

import lombok.Getter;

@Getter
public class BalanceTalkException extends RuntimeException {

    private final ErrorCode errorCode;

    public BalanceTalkException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
