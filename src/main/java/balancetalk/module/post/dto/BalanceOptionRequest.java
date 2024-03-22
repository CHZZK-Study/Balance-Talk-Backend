package balancetalk.module.post.dto;

import balancetalk.module.file.domain.File;
import balancetalk.module.post.domain.BalanceOption;
import balancetalk.module.post.domain.BalanceOption.BalanceOptionBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceOptionRequest {

    @Schema(description = "선택지 id", example = "1")
    private Long balanceOptionId;

    @NotBlank
    @Size(max = 50)
    @Schema(description = "선택지 제목", example = "선택지 제목1")
    private String title;

    @NotBlank
    @Size(max = 100)
    @Schema(description = "선택지 내용", example = "선택지 내용1")
    private String description;

    @Schema(description = "DB에 저장되는 이미지 이름", example = "4df23447-2355-45h2-8783-7f6gd2ceb848_고양이.jpg")
    private String storedImageName;

    public BalanceOption toEntity(@Nullable File image) {
        BalanceOptionBuilder builder = BalanceOption.builder()
                .title(title)
                .description(description);
        if (image != null) {
            builder.file(image);
        }
        return builder.build();
    }

    public static BalanceOptionRequest fromEntity(BalanceOption balanceOption) {
        BalanceOptionRequestBuilder builder = BalanceOptionRequest.builder()
                .balanceOptionId(balanceOption.getId())
                .title(balanceOption.getTitle())
                .description(balanceOption.getDescription());
        if (balanceOption.getFile() != null) {
            builder.storedImageName(balanceOption.getFile().getStoredName());
        }
        return builder.build();
    }
}
