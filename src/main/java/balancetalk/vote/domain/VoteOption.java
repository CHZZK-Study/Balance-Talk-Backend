package balancetalk.vote.domain;

import balancetalk.global.exception.BalanceTalkException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import static balancetalk.global.exception.ErrorCode.INVALID_VOTE_OPTION;

public enum VoteOption {
    A, B;

    @JsonCreator
    public static VoteOption from(String value) {
        for (VoteOption option : VoteOption.values()) {
            if (option.name().equals(value)) {
                return option;
            }
        }
        throw new BalanceTalkException(INVALID_VOTE_OPTION);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
