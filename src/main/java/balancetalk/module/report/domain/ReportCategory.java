package balancetalk.module.report.domain;

public enum ReportCategory {

    ADVERTISEMENT("상업적 광고"), FRAUD("사칭/사기"), ABUSE("욕설/비하"), ETC("기타");

    private final String description;

    ReportCategory(String description) {
        this.description = description;
    }
}
