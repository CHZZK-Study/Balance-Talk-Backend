package balancetalk.game.dto;

import balancetalk.game.domain.Option;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GameRequest {

    private String title;

    private List<Option> options;

    @Schema(description = "DB에 저장되는 이미지 이름", example = "4df23447-2355-45h2-8783-7f6gd2ceb848_고양이.jpg")
    private String storedImageName;

    // TODO: 태그 추가해야 함
}
