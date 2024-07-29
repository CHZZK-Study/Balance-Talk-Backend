package balancetalk.report.domain;

import balancetalk.global.exception.BalanceTalkException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import static balancetalk.global.exception.ErrorCode.INVALID_REPORT_TYPE;

public enum ReportType {
    COMMENT;

    @JsonCreator
    public static ReportType from(String value) {
        for (ReportType type : ReportType.values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        throw new BalanceTalkException(INVALID_REPORT_TYPE);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
