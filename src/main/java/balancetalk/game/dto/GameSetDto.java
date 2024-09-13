package balancetalk.game.dto;

import balancetalk.game.domain.GameSet;
import balancetalk.game.domain.MainTag;
import balancetalk.game.dto.GameDto.CreateGameRequest;
import balancetalk.game.dto.GameDto.GameDetailResponse;
import balancetalk.member.domain.Member;
import balancetalk.vote.domain.VoteOption;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class GameSetDto {

    @Data
    public static class CreateGameSetRequest {

        @Schema(description = "밸런스 게임 메인 태그", example = "커플")
        private String mainTag;

        @Schema(description = "밸런스 게임 서브 태그", example = "커플지옥")
        private String subTag;

        private List<CreateGameRequest> games;

        public GameSet toEntity(MainTag mainTag, Member member) {
            return GameSet.builder()
                    .mainTag(mainTag)
                    .subTag(subTag)
                    .member(member)
                    .games(games.stream().map(CreateGameRequest::toEntity).toList())
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "밸런스 게임 세트 상세 조회 응답")
    public static class GameSetDetailResponse {

        private List<GameDetailResponse> gameDetailResponses;

        public static GameSetDetailResponse fromEntity(GameSet gameSet, Map<Long, Boolean> bookmarkMap, Map<Long, VoteOption> voteOptionMap) {
            return GameSetDetailResponse.builder()
                    .gameDetailResponses(gameSet.getGames().stream()
                            .map(game -> GameDetailResponse.fromEntity(game,
                                    bookmarkMap.getOrDefault(game.getId(), false),
                                    voteOptionMap.get(game.getId())))
                            .toList())
                    .build();
        }
    }
}
