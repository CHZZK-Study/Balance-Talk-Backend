package balancetalk.module.vote.dto;

import balancetalk.module.member.domain.Member;
import balancetalk.module.post.domain.BalanceOption;
import balancetalk.module.vote.domain.Vote;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteRequest {

    @Schema(description = "투표한 선택지 id", example = "23")
    private Long selectedOptionId;

    public Vote toEntity(BalanceOption balanceOption) {
        return Vote.builder()
                .balanceOption(balanceOption)
                .build();
    }

    public Vote toEntityWithMember(BalanceOption balanceOption, Member member) {
        return Vote.builder()
                .balanceOption(balanceOption)
                .member(member)
                .build();
    }
}
