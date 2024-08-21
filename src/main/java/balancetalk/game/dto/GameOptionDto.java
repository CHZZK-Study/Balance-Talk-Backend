package balancetalk.game.dto;

import balancetalk.game.domain.GameOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "밸런스 게임 선택지")
public class GameOptionDto {

    private String name;

    private String imgUrl;

    private String description;

    public GameOption toEntity() {
        return GameOption.builder()
                .name(name)
                .imgUrl(imgUrl)
                .description(description)
                .build();
    }
}
