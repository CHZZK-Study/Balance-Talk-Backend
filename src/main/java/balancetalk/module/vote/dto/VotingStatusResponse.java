package balancetalk.module.vote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class VotingStatusResponse {

    @Schema(description = "선택지 제목", example = "선택지1 제목")
    private String optionTitle;

    @Schema(description = "회원 득표 수" , example = "3412")
    private int voteCount;
}
