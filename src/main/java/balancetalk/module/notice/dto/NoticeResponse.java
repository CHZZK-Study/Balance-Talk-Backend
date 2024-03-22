package balancetalk.module.notice.dto;

import balancetalk.module.file.domain.File;
import balancetalk.module.notice.domain.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class NoticeResponse {

    @Schema(description = "게시글 id", example = "1")
    private Long id;

    @Schema(description = "게시글 제목", example = "게시글 제목")
    private String title;

    @Schema(description = "게시글 내용", example = "게시글 내용")
    private String content;

    @Schema(description = "파일 리스트", example = "[\"4df23447-2355-45h2-8783-7f6gd2ceb848_고양이.jpg\", \"4df23447-2355-45h2-8783-7f6gd2ceb848_강아지.jpg\"]")
    private List<String> storedFileNames;

    @Schema(description = "게시글 작성일", example = "2022-02-12")
    private LocalDateTime createdAt;

    @Schema(description = "게시글 작성자", example = "작성자 닉네임")
    private String createdBy;

    public static NoticeResponse fromEntity(Notice notice, List<String> storedFileNames) {
        return NoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .createdAt(notice.getCreatedAt())
                .createdBy(notice.getMember().getNickname())
                .storedFileNames(storedFileNames)
                .build();
    }
}
