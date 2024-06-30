package balancetalk.vote.dto;

import balancetalk.vote.domain.VoteOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VoteRequest {

    @Schema(description = "투표할 선택지", example = "A")
    private VoteOption voteOption;
}
