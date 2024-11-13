package balancetalk.game.dto;

import balancetalk.file.domain.File;
import balancetalk.file.domain.repository.FileRepository;
import balancetalk.game.domain.GameOption;
import balancetalk.game.domain.TempGameOption;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.vote.domain.VoteOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

public class TempGameOptionDto {

    @Data
    @Builder
    @Schema(description = "밸런스 게임 옵션 생성 요청")
    public static class CreateTempGameOption {

        @Schema(description = "선택지 이름", example = "선택지 이름")
        private String name;

        @Schema(description = "선택지 이미지", example = "https://pikko-image.s3.ap-northeast-2.amazonaws.com/balance-game/067cc56e-21b7-468f-a2c1-4839036ee7cd_unnamed.png")
        private String imgUrl;

        @Schema(description = "선택지 이미지 파일 ID", example = "12")
        private Long fileId;

        @Schema(description = "선택지 추가설명", example = "선택지 추가 설명")
        private String description;

        @Schema(description = "선택지", example = "A")
        private VoteOption optionType;

        public TempGameOption toEntity(FileRepository fileRepository) {
            String newImgUrl = null;
            if (fileId != null) {
                File file = fileRepository.findById(fileId)
                        .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_FILE));
                newImgUrl = file.getS3Url();
            }
            return TempGameOption.builder()
                    .name(name)
                    .imgUrl(newImgUrl)
                    .description(description)
                    .optionType(optionType)
                    .build();
        }
    }

    @Data
    @Builder
    @Schema(description = "밸런스 게임 옵션 조회 응답")
    public static class FindTempGameOption {

        @Schema(description = "선택지 이름", example = "선택지 이름")
        private String name;

        @Schema(description = "선택지 추가설명", example = "선택지 추가 설명")
        private String description;

        @Schema(description = "선택지 이미지", example = "https://pikko-image.s3.ap-northeast-2.amazonaws.com/balance-game/067cc56e-21b7-468f-a2c1-4839036ee7cd_unnamed.png")
        private String imgUrl;

        @Schema(description = "선택지", example = "A")
        private VoteOption optionType;

        public static FindTempGameOption fromEntity(TempGameOption tempGameOption) {
            return FindTempGameOption.builder()
                    .name(tempGameOption.getName())
                    .imgUrl(tempGameOption.getImgUrl())
                    .description(tempGameOption.getDescription())
                    .optionType(tempGameOption.getOptionType())
                    .build();
        }
    }
}