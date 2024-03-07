package balancetalk.global.exception;

import static org.springframework.http.HttpStatus.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400
    MISMATCHED_BALANCE_OPTION(BAD_REQUEST, "투표한 선택지는 다른 게시글에 속해 있습니다."),
    EXPIRED_POST_DEADLINE(BAD_REQUEST, "투표가 이미 종료된 게시글입니다."),
    UNMODIFIABLE_VOTE(BAD_REQUEST, "투표 수정이 불가능한 게시글입니다."),
    ALREADY_BOOKMARK(BAD_REQUEST, "이미 북마크한 게시글입니다."),
    INCORRECT_PASSWORD(BAD_REQUEST, "비밀번호가 잘못되었거나 요청 형식이 올바르지 않습니다"),
    MIME_TYPE_NULL(BAD_REQUEST, "MIME 타입이 null입니다."),
    FILE_UPLOAD_FAILED(BAD_REQUEST, "파일 업로드에 실패했습니다."),
    FILE_SIZE_EXCEEDED(BAD_REQUEST, "파일 크기가 초과되었습니다."),
    EXPIRED_JWT_TOKEN(BAD_REQUEST, "만료된 토큰 입니다."),
    INVALID_JWT_TOKEN(BAD_REQUEST, "유효하지 않은 토큰입니다."),
    EXCEED_MAX_DEPTH(BAD_REQUEST, "답글에 답글을 달 수 없습니다."),
    INVALID_REFRESH_TOKEN(BAD_REQUEST, "유효하지 않은 리프레시 토큰입니다."),

    // 401
    MISMATCHED_EMAIL_OR_PASSWORD(UNAUTHORIZED, "이메일 또는 비밀번호가 잘못되었습니다."),
    AUTHENTICATION_ERROR(UNAUTHORIZED, "인증 오류가 발생했습니다."),
    BAD_CREDENTIAL_ERROR(UNAUTHORIZED, "로그인에 실패했습니다."),
    UNAUTHORIZED_LOGOUT(UNAUTHORIZED, "로그아웃을 위해서는 인증이 필요합니다."),

    // 403
    FORBIDDEN_POST_DELETE(FORBIDDEN, "해당 게시글은 삭제 권한이 없습니다."),
    FORBIDDEN_COMMENT_MODIFY(FORBIDDEN, "해당 댓글은 수정 권한이 없습니다."),
    FORBIDDEN_COMMENT_DELETE(FORBIDDEN, "해당 댓글은 삭제 권한이 없습니다."),
    FORBIDDEN_MEMBER_DELETE(FORBIDDEN, "사용자 탈퇴 권한이 없습니다."),
    FORBIDDEN_BOOKMARK_DELETE(FORBIDDEN, "북마크 삭제 권한이 없습니다."),
    FORBIDDEN_POST_CREATE(FORBIDDEN, "글쓰기 권한이 없습니다."),
    FORBIDDEN_OWN_REPORT(FORBIDDEN, "자신의 게시글 또는 댓글을 신고할 수 없습니다."),

    // 404
    NOT_FOUND_POST(NOT_FOUND, "존재하지 않는 게시글입니다."),
    NOT_FOUND_BALANCE_OPTION(NOT_FOUND, "존재하지 않는 선택지입니다."),
    NOT_FOUND_MEMBER(NOT_FOUND, "존재하지 않는 회원입니다."),
    NOT_FOUND_VOTE(NOT_FOUND, "해당 게시글에서 투표한 기록이 존재하지 않습니다."),
    NOT_FOUND_BOOKMARK(NOT_FOUND, "해당 게시글에서 북마크한 기록이 존재하지 않습니다."),
    NOT_FOUND_COMMENT(NOT_FOUND, "존재하지 않는 댓글입니다."),
    NOT_FOUND_DIRECTORY(NOT_FOUND, "존재하지 않는 디렉토리입니다."),
    NOT_SUPPORTED_FILE_TYPE(NOT_FOUND, "지원하지 않는 파일 형식입니다."),
    NOT_FOUND_FILE(NOT_FOUND, "존재하지 않는 파일입니다."),
    NOT_FOUND_PARENT_COMMENT(NOT_FOUND, "존재하지 않는 원 댓글입니다."),
    NOT_FOUND_COMMENT_AT_THAT_POST(NOT_FOUND, "해당 게시글에 존재하지 않는 댓글입니다."),

    // 409
    ALREADY_VOTE(CONFLICT, "이미 투표한 게시글입니다."),
    ALREADY_LIKE_COMMENT(CONFLICT, "이미 추천을 누른 댓글입니다."),
    ALREADY_LIKE_POST(CONFLICT, "이미 추천을 누른 게시글입니다."),
    ALREADY_CANCEL_LIKE_POST(CONFLICT, "이미 추천 취소를 누른 게시글입니다"),

    // 500
    REDIS_CONNECTION_FAIL(INTERNAL_SERVER_ERROR, "Redis 연결에 실패했습니다."),
    DUPLICATE_EMAIL(INTERNAL_SERVER_ERROR, "이미 존재하는 이메일 입니다. 다른 이메일을 입력해주세요"),
    AUTHORIZATION_CODE_MISMATCH(INTERNAL_SERVER_ERROR, "인증 번호가 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
