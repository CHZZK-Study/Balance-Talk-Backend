package balancetalk.module.vote.dto;

import balancetalk.module.member.domain.Member;
import balancetalk.module.post.domain.BalanceOption;
import balancetalk.module.vote.domain.Vote;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class VoteRequest {

    @Schema(description = "투표한 선택지 id", example = "2")
    private Long selectedOptionId;

    @Schema(description = "회원 여부", example = "true")
    private boolean isUser;

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
