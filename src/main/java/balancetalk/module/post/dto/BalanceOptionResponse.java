package balancetalk.module.post.dto;

import balancetalk.module.file.domain.File;
import balancetalk.module.post.domain.BalanceOption;
import balancetalk.module.post.domain.BalanceOption.BalanceOptionBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceOptionResponse {

    @Schema(description = "선택지 id", example = "1")
    private Long balanceOptionId;

    @Schema(description = "선택지 제목", example = "선택지 제목1")
    private String title;

    @Schema(description = "선택지 내용", example = "선택지 내용1")
    private String description;

    @Schema(description = "이미지 URL",
            example = "https://balance-talk-static-files4df23447-2355-45h2-8783-7f6gd2ceb848_고양이.jpg")
    private String imageUrl;

    public BalanceOption toEntity(@Nullable File image) {
        BalanceOptionBuilder builder = BalanceOption.builder()
                .title(title)
                .description(description);
        if (image != null) {
            builder.file(image);
        }
        return builder.build();
    }

    public static BalanceOptionResponse fromEntity(BalanceOption balanceOption) {
        BalanceOptionResponseBuilder builder = BalanceOptionResponse.builder()
                .title(balanceOption.getTitle())
                .description(balanceOption.getDescription());
        if (balanceOption.getFile() != null) {
            builder.imageUrl(balanceOption.getFile().getUrl());
        }
        return builder.build();
    }
}
