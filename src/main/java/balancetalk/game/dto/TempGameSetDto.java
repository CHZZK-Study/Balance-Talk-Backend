package balancetalk.game.dto;

import balancetalk.game.domain.TempGameSet;
import balancetalk.game.dto.TempGameDto.CreateTempGameRequest;
import balancetalk.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

public class TempGameSetDto {

    @Data
    public static class CreateTempGameSetRequest {

        @Schema(description = "밸런스게임 세트 제목", example = "밸런스게임 세트 제목")
        private String title;

        private List<CreateTempGameRequest> tempGames;

        public TempGameSet toEntity(String title, Member member) {
            return TempGameSet.builder()
                    .title(title)
                    .member(member)
                    .bookmarks(0L)
                    .editedAt(LocalDateTime.now())
                    .build();
        }
    }

    @Data
    @Builder
    @Schema(description = "임시 밸런스 게임 세트 조회 응답")
    public static class TempGameSetResponse {

    }
}
