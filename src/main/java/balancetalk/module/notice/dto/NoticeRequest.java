package balancetalk.module.notice.dto;

import balancetalk.module.file.domain.File;
import balancetalk.module.member.domain.Member;
import balancetalk.module.notice.domain.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.List;

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

    @Schema(description = "파일 리스트", example = "[\"4df23447-2355-45h2-8783-7f6gd2ceb848_고양이.jpg\"," +
            " \"4df23447-2355-45h2-8783-7f6gd2ceb848_강아지.jpg\"]")
    private List<String> storedFileNames;

    public Notice toEntity(Member member) {
        return Notice.builder()
                .title(this.title)
                .content(this.content)
                .member(member)
                .build();
    }
}
