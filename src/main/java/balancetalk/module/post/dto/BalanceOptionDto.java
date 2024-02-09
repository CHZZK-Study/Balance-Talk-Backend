package balancetalk.module.post.dto;

import balancetalk.module.file.dto.FileDto;
import balancetalk.module.post.domain.BalanceOption;
import lombok.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceOptionDto {
    private String title;
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
