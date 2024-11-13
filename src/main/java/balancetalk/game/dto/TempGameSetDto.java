package balancetalk.game.dto;

import balancetalk.game.domain.MainTag;
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

        @Schema(description = "밸런스 게임 메인 태그", example = "커플")
        private String mainTag;

        @Schema(description = "밸런스 게임 서브 태그", example = "커플지옥")
        private String subTag;

        private List<CreateTempGameRequest> tempGames;

        public TempGameSet toEntity(String title, MainTag mainTag, Member member) {
            return TempGameSet.builder()
                    .title(title)
                    .mainTag(mainTag)
                    .subTag(subTag)
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
