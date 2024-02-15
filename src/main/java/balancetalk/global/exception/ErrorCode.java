package balancetalk.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 400
    MISMATCHED_BALANCE_OPTION(BAD_REQUEST, "선택한 선택지는 다른 게시글에 속해 있습니다."),
    EXPIRED_POST_DEADLINE(BAD_REQUEST, "투표가 이미 종료된 게시글입니다."),
    UNMODIFIABLE_VOTE(BAD_REQUEST, "투표 수정이 불가능한 게시글입니다."),

    // 401
    MISMATCHED_EMAIL_OR_PASSWORD(UNAUTHORIZED, "이메일 또는 비밀번호가 잘못되었습니다."),
    AUTHENTICATION_ERROR(UNAUTHORIZED, "인증 오류가 발생했습니다."),

    // 404
    NOT_FOUND_POST(NOT_FOUND, "존재하지 않는 게시글입니다."),
    NOT_FOUND_BALANCE_OPTION(NOT_FOUND, "존재하지 않는 선택지입니다."),
    NOT_FOUND_MEMBER(NOT_FOUND, "존재하지 않는 회원입니다."),
    NOT_FOUND_VOTE(NOT_FOUND, "해당 게시글에서 투표한 기록이 존재하지 않습니다."),
    NOT_FOUND_MEMBER_INFORMATION(NOT_FOUND, "회원 정보를 찾을 수 없습니다"),


    // 409
    ALREADY_VOTE(CONFLICT, "투표는 한 번만 가능합니다."),
    ALREADY_LIKE_COMMENT(CONFLICT, "이미 추천을 누른 댓글입니다."),
    ALREADY_LIKE_POST(CONFLICT, "이미 추천을 누른 게시글입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
