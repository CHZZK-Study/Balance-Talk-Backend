package balancetalk.game.dto;

import static balancetalk.vote.domain.VoteOption.*;

import balancetalk.bookmark.domain.Bookmark;
import balancetalk.game.domain.Game;
import balancetalk.game.domain.MainTag;
import balancetalk.member.domain.Member;
import balancetalk.vote.domain.GameVote;
import balancetalk.vote.domain.VoteOption;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
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

        @Schema(description = "밸런스 게임 메인 태그", example = "커플")
        private String mainTag;

        @Schema(description = "밸런스 게임 서브 태그", example = "커플지옥")
        private String subTag;

        private List<GameOptionDto> gameOptions;

        public Game toEntity(MainTag mainTag, Member member) {
            return Game.builder()
                    .title(title)
                    .description(description)
                    .mainTag(mainTag)
                    .subTag(subTag)
                    .gameOptions(gameOptions.stream().map(GameOptionDto::toEntity).toList())
                    .member(member)
                    .bookmarks(0L)
                    .views(0L)
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

        private List<GameOptionDto> gameOptions;

        @Schema(description = "메인태그", example = "커플")
        private String mainTag;

        @Schema(description = "서브태그", example = "커플지옥")
        private String subTag;

        @Schema(description = "북마크 여부", example = "false")
        private Boolean myBookmark;

        public static GameResponse fromEntity(Game game, Member member, boolean isBookmarked) {
            return GameResponse.builder()
                    .id(game.getId())
                    .title(game.getTitle())
                    .description(game.getDescription())
                    .gameOptions(game.getGameOptions().stream().map(GameOptionDto::fromEntity).toList())
                    .mainTag(game.getMainTag().getName())
                    .subTag(game.getSubTag())
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

        @Schema(description = "메인태그", example = "커플")
        private String mainTag;

        @Schema(description = "서브태그", example = "커플지옥")
        private String subTag;

        @Schema(description = "작성자 닉네임", example = "작성자")
        private String writer;

        @Schema(description = "작성일", example = "2024-09-05")
        private LocalDate createdAt;

        public static GameDetailResponse from(Game game, boolean myBookmark, VoteOption votedOption) {
            return GameDetailResponse.builder()
                    .id(game.getId())
                    .title(game.getTitle())
                    .description(game.getDescription())
                    .gameOptions(game.getGameOptions().stream().map(GameOptionDto::fromEntity).toList())
                    .views(game.getViews())
                    .votesCountOfOptionA(game.getVoteCount(A))
                    .votesCountOfOptionB(game.getVoteCount(B))
                    .myBookmark(myBookmark)
                    .votedOption(votedOption)
                    .mainTag(game.getMainTag().getName())
                    .subTag(game.getSubTag())
                    .writer(game.getWriter())
                    .createdAt(game.getCreatedAt().toLocalDate())
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "밸런스 게임 메인 태그 생성")
    public static class CreateGameMainTagRequest {

        @Schema(description = "밸런스 게임 메인 태그", example = "커플")
        private String name;

        public MainTag toEntity() {
            return MainTag.builder()
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
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
        private LocalDateTime editedAt;

        @Schema(description = "북마크 여부")
        private boolean isBookmarked;

        @Schema(description = "밸런스 게임 서브 태그", example = "화제의 중심")
        private String subTag;

        @Schema(description = "밸런스 게임 메인 태그", example = "인기")
        private String mainTag;

        public static GameMyPageResponse from(Game game) {
            return GameMyPageResponse.builder()
                    .id(game.getId())
                    .title(game.getTitle())
                    .optionAImg(game.getGameOptions().get(0).getImgUrl())
                    .optionBImg(game.getGameOptions().get(1).getImgUrl())
                    .subTag(game.getSubTag())
                    .mainTag(game.getMainTag().getName())
                    .editedAt(game.getEditedAt())
                    .build();
        }

        public static GameMyPageResponse from(Game game, Bookmark bookmark) {
            return GameMyPageResponse.builder()
                    .id(game.getId())
                    .title(game.getTitle())
                    .optionAImg(game.getGameOptions().get(0).getImgUrl())
                    .optionBImg(game.getGameOptions().get(1).getImgUrl())
                    .isBookmarked(bookmark.isActive())
                    .subTag(game.getSubTag())
                    .mainTag(game.getMainTag().getName())
                    .editedAt(game.getEditedAt())
                    .build();
        }

        public static GameMyPageResponse from(Game game, GameVote vote) {
            return GameMyPageResponse.builder()
                    .id(game.getId())
                    .title(game.getTitle())
                    .optionAImg(game.getGameOptions().get(0).getImgUrl())
                    .optionBImg(game.getGameOptions().get(1).getImgUrl())
                    .voteOption(vote.getVoteOption())
                    .subTag(game.getSubTag())
                    .mainTag(game.getMainTag().getName())
                    .editedAt(game.getEditedAt())
                    .build();
        }
    }
}
