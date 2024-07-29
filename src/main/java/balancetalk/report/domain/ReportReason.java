package balancetalk.report.domain;

import balancetalk.global.exception.BalanceTalkException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import static balancetalk.global.exception.ErrorCode.INVALID_REPORT_REASON;


public enum ReportReason {
    욕설, 도배_및_스팸, 불건전_및_불법_정보, 차별적_발언, 홍보, 개인정보_노출_및_침해, 기타;

    @JsonCreator
    public static ReportReason from(String value) {
        for (ReportReason reason : ReportReason.values()) {
            if (reason.name().equals(value)) {
                return reason;
            }
        }
        throw new BalanceTalkException(INVALID_REPORT_REASON);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
