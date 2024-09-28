package balancetalk.game.dto;

import balancetalk.game.domain.Game;
import balancetalk.game.domain.GameSet;
import balancetalk.game.domain.MainTag;
import balancetalk.game.dto.GameDto.CreateGameRequest;
import balancetalk.game.dto.GameDto.GameDetailResponse;
import balancetalk.member.domain.Member;
import balancetalk.vote.domain.VoteOption;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.Arrays;
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
                    .bookmarks(0L)
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "밸런스 게임 세트 목록 조회 응답")
    public static class GameSetResponse {

        @Schema(description = "밸런스 게임 세트 id", example = "1")
        private Long id;

        @Schema(description = "메인 태그", example = "사랑")
        private String mainTag;

        @Schema(description = "서브 태그", example = "서브 태그")
        private String subTag;

        @Schema(description = "게시글 제목", example = "제목")
        private String title;

        private List<String> images;

        public static GameSetResponse fromEntity(GameSet gameSet) {
            Game game = gameSet.getGames().get(0);
            List<String> images = new ArrayList<>();
            images.addAll(Arrays.asList(
                    game.getGameOptions().get(0).getImgUrl(),
                    game.getGameOptions().get(1).getImgUrl()
            ));
            return GameSetResponse.builder()
                    .id(gameSet.getId())
                    .mainTag(gameSet.getMainTag().getName())
                    .subTag(gameSet.getSubTag())
                    .title(game.getTitle())
                    .images(images)
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "밸런스 게임 세트 상세 조회 응답")
    public static class GameSetDetailResponse {

        private List<GameDetailResponse> gameDetailResponses;

        @Schema(description = "밸런스게임 세트 전체 투표 완료 여부", example = "false")
        @JsonProperty("isEndGameSet")
        private boolean isEndGameSet;

        public static GameSetDetailResponse fromEntity(GameSet gameSet, Map<Long, Boolean> bookmarkMap,
                                                       Map<Long, VoteOption> voteOptionMap, boolean isEndGameSet) {

            return GameSetDetailResponse.builder()
                    .isEndGameSet(isEndGameSet)
                    .gameDetailResponses(gameSet.getGames().stream()
                            .map(game -> GameDetailResponse.fromEntity(game,
                                    bookmarkMap.getOrDefault(game.getId(), false),
                                    voteOptionMap.get(game.getId())))
                            .toList())
                    .build();
        }
    }
}
