package balancetalk.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 400
    INVALID_POST_TITLE(BAD_REQUEST, "유효하지 않은 제목입니다."),
    INVALID_COMMENT_$$$(BAD_REQUEST, "댓글의 길이는 최대 ~~입니다."),

    // 409
    INVALID_$$$(CONFLICT, "이미 존재하는 ~~~입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}