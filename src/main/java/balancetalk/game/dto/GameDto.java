package balancetalk.game.dto;

import static balancetalk.vote.domain.VoteOption.*;

import balancetalk.bookmark.domain.Bookmark;
import balancetalk.game.domain.Game;
import balancetalk.game.domain.GameTopic;
import balancetalk.member.domain.Member;
import balancetalk.vote.domain.Vote;
import balancetalk.vote.domain.VoteOption;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

public class GameDto {

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "밸런스 게임 생성 요청")
    public static class CreateGameRequest {

        @Schema(description = "제목", example = "제목")
        private String title;

        @Schema(description = "게임 추가 설명", example = "추가 설명")
        private String description;

        @Schema(description = "밸런스 게임 서브 태그", example = "커플지옥")
        private String tag;

        @Schema(description = "밸런스 게임 메인 태그", example = "커플")
        private String gameTopic;

        private List<GameOptionDto> gameOptions;

        public Game toEntity(GameTopic gameTopic, Member member) {
            return Game.builder()
                    .title(title)
                    .description(description)
                    .tag(Optional.ofNullable(tag).orElse(""))
                    .gameTopic(gameTopic)
                    .gameOptions(gameOptions.stream().map(GameOptionDto::toEntity).collect(Collectors.toUnmodifiableList()))
                    .member(member)
                    .editedAt(LocalDateTime.now())
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "밸런스 게임 조회 응답")
    public static class GameResponse {

        @Schema(description = "밸런스 게임 id", example = "1")
        private Long id;

        @Schema(description = "밸런스 게임 제목", example = "제목")
        private String title;

        @Schema(description = "게임 추가 설명", example = "추가 설명")
        private String description;

        @Schema(description = "밸런스 게임 서브 태그", example = "커플지옥")
        private String tag;

        private List<GameOptionDto> gameOptions;

        @Schema(description = "카테고리", example = "월드컵")
        private String gameTopic;

        @Schema(description = "북마크 여부", example = "false")
        private Boolean myBookmark;

        public static GameResponse fromEntity(Game game, Member member, boolean isBookmarked) {
            return GameResponse.builder()
                    .id(game.getId())
                    .title(game.getTitle())
                    .description(game.getDescription())
                    .tag(game.getTag())
                    .gameOptions(game.getGameOptions().stream().map(GameOptionDto::fromEntity).collect(Collectors.toUnmodifiableList()))
                    .gameTopic(game.getGameTopic().getName())
                    .myBookmark(isBookmarked)
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "밸런스 게임 상세 조회 응답")
    public static class GameDetailResponse {

        @Schema(description = "밸런스 게임 id", example = "1")
        private Long id;

        @Schema(description = "밸런스 게임 제목", example = "제목")
        private String title;

        @Schema(description = "게임 추가 설명", example = "추가 설명")
        private String description;

        @Schema(description = "밸런스 게임 서브 태그", example = "커플지옥")
        private String tag;

        private List<GameOptionDto> gameOptions;

        @Schema(description = "조회수", example = "3")
        private long views;

        @Schema(description = "선택지 A 투표수", example = "98")
        private long votesCountOfOptionA;

        @Schema(description = "선택지 B 투표수", example = "95")
        private long votesCountOfOptionB;

        @Schema(description = "북마크 여부", example = "false")
        private Boolean myBookmark;

        @Schema(description = "투표한 선택지", example = "A")
        private VoteOption votedOption;

        @Schema(description = "카테고리", example = "월드컵")
        private String gameTopic;

        public static GameDetailResponse from(Game game, boolean myBookmark, VoteOption votedOption) {
            return GameDetailResponse.builder()
                    .id(game.getId())
                    .title(game.getTitle())
                    .description(game.getDescription())
                    .tag(Optional.ofNullable(game.getTag()).orElse(""))
                    .gameOptions(game.getGameOptions().stream().map(GameOptionDto::fromEntity)
                            .collect(Collectors.toUnmodifiableList()))
                    .views(game.getViews())
                    .votesCountOfOptionA(game.getVoteCount(A))
                    .votesCountOfOptionB(game.getVoteCount(B))
                    .myBookmark(myBookmark)
                    .votedOption(votedOption)
                    .gameTopic(game.getGameTopic().getName())
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "밸런스 게임 주제 생성")
    public static class CreateGameTopicRequest {

        @Schema(description = "밸런스 게임 주제", example = "커플")
        private String name;

        public GameTopic toEntity() {
            return GameTopic.builder()
                    .name(name)
                    .build();
        }
    }

    @Schema(description = "마이페이지 밸런스 게임 응답")
    @Data
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GameMyPageResponse {

        @Schema(description = "밸런스 게임 ID", example = "1")
        private long id;

        @Schema(description = "밸런스 게임 제목", example = "제목")
        private String title;

        @Schema(description = "투표한 선택지", example = "A")
        private VoteOption voteOption;

        @Schema(description = "선택지 A 이미지", example = "https://pikko-image.s3.ap-northeast-2.amazonaws.com/balance-game/067cc56e-21b7-468f-a2c1-4839036ee7cd_unnamed.png")
        private String optionAImg;

        @Schema(description = "선택지 B 이미지", example = "https://pikko-image.s3.ap-northeast-2.amazonaws.com/balance-game/1157461e-a685-42fd-837e-7ed490894ca6_unnamed.png")
        private String optionBImg;

        @Schema(description = "최종 수정일(마이페이지 등록 날짜)")
        private LocalDateTime editedAt;

        @Schema(description = "북마크 여부")
        private boolean isBookmarked;

        public static GameMyPageResponse from(Game game) {
            return GameMyPageResponse.builder()
                    .id(game.getId())
                    .title(game.getTitle())
//                    .optionAImg(game.getOptionAImg())
//                    .optionBImg(game.getOptionBImg())
                    .editedAt(game.getEditedAt())
                    .build();
        }

        public static GameMyPageResponse from(Game game, Bookmark bookmark) { // TODO : 클라이언트에게 어떤 정보를 제공할지 추후 기능명세서 업데이트 후 결정
            return GameMyPageResponse.builder()
                    .id(game.getId())
                    .title(game.getTitle())
//                    .optionAImg(game.getOptionAImg())
//                    .optionBImg(game.getOptionBImg())
                    .isBookmarked(bookmark.isActive())
                    .editedAt(game.getEditedAt())
                    .build();
        }

        public static GameMyPageResponse from(Game game, Vote vote) {
            return GameMyPageResponse.builder()
                    .id(game.getId())
                    .title(game.getTitle())
//                    .optionAImg(game.getOptionAImg())
//                    .optionBImg(game.getOptionBImg())
                    .voteOption(vote.getVoteOption())
                    .editedAt(game.getEditedAt())
                    .build();
        }
    }

}
