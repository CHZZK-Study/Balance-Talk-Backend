package balancetalk.module.notice.dto;

import balancetalk.module.member.domain.Member;
import balancetalk.module.notice.domain.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NoticeRequest {

    @NotBlank
    @Size(max = 50)
    @Schema(description = "공지사항 제목", example = "공지사항 제목")
    private String title;

    @NotBlank
    @Size(max = 2000)
    @Schema(description = "공지사항 내용", example = "공지사항 내용")
    private String content;

    public Notice toEntity(Member member) {

        return Notice.builder()
                .title(title)
                .content(content)
                .member(member)
                .build();
    }
}
