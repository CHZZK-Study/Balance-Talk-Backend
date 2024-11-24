package balancetalk.game.dto;

import balancetalk.file.domain.repository.FileRepository;
import balancetalk.game.domain.MainTag;
import balancetalk.game.domain.TempGameSet;
import balancetalk.game.dto.TempGameDto.CreateTempGameRequest;
import balancetalk.game.dto.TempGameDto.TempGameResponse;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class TempGameSetDto {

    private static final int GAME_SIZE = 10;

    @Data
    public static class CreateTempGameSetRequest {

        @Schema(description = "밸런스게임 세트 제목", example = "밸런스게임 세트 제목")
        private String title;

        @Schema(description = "밸런스 게임 메인 태그", example = "커플")
        private String mainTag;

        @Schema(description = "밸런스 게임 서브 태그", example = "커플지옥")
        private String subTag;

        private List<CreateTempGameRequest> tempGames;

        public TempGameSet toEntity(MainTag mainTag, Member member) {
            if (tempGames == null || tempGames.size() < GAME_SIZE) {
                throw new BalanceTalkException(ErrorCode.BALANCE_GAME_SIZE_TEN);
            }
            return TempGameSet.builder()
                    .title(title)
                    .mainTag(mainTag)
                    .subTag(subTag)
                    .member(member)
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "임시 밸런스 게임 세트 조회 응답")
    public static class TempGameSetResponse {

        @Schema(description = "밸런스게임 세트 제목", example = "밸런스게임 세트 제목")
        private String title;

        @Schema(description = "밸런스 게임 메인 태그", example = "커플")
        private String mainTag;

        @Schema(description = "밸런스 게임 서브 태그", example = "커플지옥")
        private String subTag;

        @Schema(description = "게임 리스트")
        private List<TempGameResponse> tempGames;

        public static TempGameSetResponse fromEntity(TempGameSet tempGameSet, FileRepository fileRepository) {

            List<TempGameResponse> tempGames = tempGameSet.getTempGames().stream()
                    .map(tempGame -> TempGameResponse.fromEntity(tempGame, fileRepository))
                    .toList();

            return TempGameSetResponse.builder()
                    .title(tempGameSet.getTitle())
                    .mainTag(tempGameSet.getMainTag().getName())
                    .subTag(tempGameSet.getSubTag())
                    .tempGames(tempGames)
                    .build();
        }

    }
}
