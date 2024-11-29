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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class TempGameSetDto {

    private static final int GAME_SIZE = 10;

    @Data
    public static class CreateTempGameSetRequest {

        @Schema(description = "밸런스게임 세트 제목", example = "밸런스게임 세트 제목")
        @Size(max = 50, message = "제목은 최대 50자까지 입력 가능합니다")
        private String title;

        @Schema(description = "밸런스 게임 메인 태그", example = "커플")
        private String mainTag;

        @Schema(description = "밸런스 게임 서브 태그", example = "커플지옥")
        @Size(max = 10, message = "서브 태그는 최대 10자까지 입력 가능합니다")
        private String subTag;

        @Schema(description = "최근 임시저장된 밸런스 게임 불러오기 여부", example = "true")
        @NotNull(message = "isLoaded 필드는 NULL을 허용하지 않습니다.")
        private Boolean isLoaded;

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

        public List<Long> getAllFileIds() {
            return tempGames.stream()
                    .flatMap(game -> game.getTempGameOptions().stream())
                    .map(TempGameOptionDto::getFileId)
                    .filter(Objects::nonNull)
                    .toList();
        }

        public boolean isNewRequest() {
            return !isLoaded;
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
