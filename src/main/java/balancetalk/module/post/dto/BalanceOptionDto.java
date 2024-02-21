package balancetalk.module.post.dto;

import balancetalk.module.file.dto.FileDto;
import balancetalk.module.post.domain.BalanceOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceOptionDto {

    @Schema(description = "선택지 제목", example = "선택지 제목1")
    private String title;

    @Schema(description = "선택지 내용", example = "선택지 내용1")
    private String description;
    private FileDto file;

    public BalanceOption toEntity() {
        return BalanceOption.builder()
                .title(title)
                .description(description)
                .file(file.toEntity())
                .build();
    }

    public static BalanceOptionDto fromEntity(BalanceOption balanceOption) {
        FileDto fileDto = FileDto.fromEntity(balanceOption.getFile());
        return BalanceOptionDto.builder()
                .title(balanceOption.getTitle())
                .description(balanceOption.getDescription())
                .file(fileDto)
                .build();
    }
}
