package balancetalk.game.dto;

import balancetalk.game.domain.Option;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GameResponse {
    @Schema(description = "밸런스 게임 id", example = "1")
    private Long id;

    @Schema(description = "밸런스 게임 제목", example = "제목")
    private String title;

    @Schema(description = "투표한 선택지 이름", example = "A")
    private Option option;

    @Schema(description = "북마크 여부", example = "false")
    private boolean myBookmark;

    @Schema(description = "투표 여부", example = "true")
    private boolean myVote;

    @Schema(description = "이미지 URL",
            example = "https://balance-talk-static-files4df23447-2355-45h2-8783-7f6gd2ceb848_고양이.jpg")
    private String imageUrl;
}
