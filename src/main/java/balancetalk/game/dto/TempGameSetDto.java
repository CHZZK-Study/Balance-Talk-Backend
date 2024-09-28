package balancetalk.game.dto;

import balancetalk.game.domain.MainTag;
import balancetalk.game.domain.TempGameSet;
import balancetalk.game.dto.TempGameDto.CreateTempGameRequest;
import balancetalk.game.dto.TempGameDto.TempGameDetailResponse;
import balancetalk.game.dto.TempGameOptionDto.CreateTempGameOption;
import balancetalk.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Data;

public class TempGameSetDto {

    @Data
    public static class CreateTempGameSetRequest {

        @Schema(description = "밸런스 게임 메인 태그", example = "커플")
        private String mainTag;

        @Schema(description = "밸런스 게임 서브 태그", example = "커플지옥")
        private String subTag;

        private List<CreateTempGameRequest> tempGames;

        public TempGameSet toEntity(MainTag mainTag, Member member) {
            return TempGameSet.builder()
                    .mainTag(mainTag)
                    .subTag(subTag)
                    .member(member)
                    .tempGames(tempGames.stream().map(CreateTempGameRequest::toEntity).toList())
                    .build();
        }

        public List <String> extractStoredNames() {
            return this.getTempGames().stream()
                    .flatMap(tempGame -> tempGame.getTempGameOptions().stream())
                    .map(CreateTempGameOption::getStoredName)
                    .toList();
        }
    }

    @Data
    @Builder
    @Schema(description = "임시 밸런스 게임 세트 조회 응답")
    public static class TempGameSetResponse {

        private List<TempGameDetailResponse> tempGameDetailResponses;

        public static TempGameSetResponse fromEntity(TempGameSet tempGameSet) {
            return TempGameSetResponse.builder()
                    .tempGameDetailResponses(tempGameSet.getTempGames().stream()
                            .map(tempGame -> TempGameDetailResponse.fromEntity(tempGame)).toList()
                    ).build();
        }
    }
}
