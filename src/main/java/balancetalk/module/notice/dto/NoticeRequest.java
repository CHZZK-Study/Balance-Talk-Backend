package balancetalk.module.notice.dto;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.module.member.domain.Member;
import balancetalk.module.notice.domain.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static balancetalk.global.exception.ErrorCode.EXCEED_NOTICE_CONTENT_LENGTH;
import static balancetalk.global.exception.ErrorCode.EXCEED_TITLE_LENGTH;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRequest {

    @Schema(description = "공지사항 제목", example = "공지사항 제목")
    private String title;

    @Schema(description = "공지사항 내용", example = "공지사항 내용")
    private String content;

    public Notice toEntity(Member member) {

        if (title.length() > 100 || title.isEmpty()) {
            throw new BalanceTalkException(EXCEED_TITLE_LENGTH);
        }

        if (content.length() > 2000 || content.isEmpty()) {
            throw new BalanceTalkException(EXCEED_NOTICE_CONTENT_LENGTH);
        }
        return Notice.builder()
                .title(title)
                .content(content)
                .member(member)
                .build();
    }
}
