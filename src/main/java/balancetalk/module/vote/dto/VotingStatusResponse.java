package balancetalk.module.vote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class VotingStatusResponse {
    private String optionTitle;
    private int voteCount;
}
