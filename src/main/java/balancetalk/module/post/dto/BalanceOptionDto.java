package balancetalk.module.post.dto;

import balancetalk.module.post.domain.BalanceOption;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BalanceOptionDto {
    private String title;
    private String description;

    public BalanceOption toEntity() {
        return BalanceOption.builder()
                .title(title)
                .description(description)
                .build();
    }
}
