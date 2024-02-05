package balancetalk.module.vote.dto;

import balancetalk.module.member.domain.Member;
import balancetalk.module.post.domain.BalanceOption;
import balancetalk.module.vote.domain.Vote;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VoteRequest {
    private Long selectedOptionId;
    private Long memberId;

    public Vote toEntity(BalanceOption balanceOption, Member member) {
        return Vote.builder()
                .balanceOption(balanceOption)
                .member(member)
                .build();
    }
}
