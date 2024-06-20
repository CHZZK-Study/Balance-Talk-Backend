package balancetalk.module.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TodayBalanceTalkResponse {

    @Schema(description = "밸런스톡 id", example = "1")
    private Long id;

    @Schema(description = "밸런스톡 제목", example = "제목")
    private String title;

    @Schema(description = "3줄 요약", example = "1.저녁 식사 중 남자친구가 친구에게 깻잎을 떼어주자 나는 불편한 감정을 느꼈다.\n" +
            "2.집에 돌아와 남자친구에게 솔직하게 말했고, 그는 미안해하며 이해해 주었다.\n" +
            "3.친구들과 이 문제를 논의했지만 결론은 나지 않았고, 사람마다 다르게 받아들일 수 있음을 깨달았다.")
    private String summary;
}
