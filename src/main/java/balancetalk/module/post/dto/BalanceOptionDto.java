package balancetalk.module.post.dto;

import balancetalk.module.post.domain.BalanceOption;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BalanceOptionDto {
    private String title;
    private String description;

    public static BalanceOptionDto fromEntity(BalanceOption balanceOption) {
        return BalanceOptionDto.builder()
                .title(balanceOption.getTitle())
                .description(balanceOption.getDescription())
                .build();
    }
    public static List<BalanceOptionDto> fromEntities(List<BalanceOption> balanceOptions) {
        return balanceOptions.stream()
                .map(BalanceOptionDto::fromEntity)
                .collect(Collectors.toList());
    }
}
