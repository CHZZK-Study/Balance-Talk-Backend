package balancetalk.module.vote.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VotingStatusResponse {
    private String optionTitle;
    private int voteCount;
}
