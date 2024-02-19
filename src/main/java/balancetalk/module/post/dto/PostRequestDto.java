package balancetalk.module.post.dto;

import balancetalk.module.ViewStatus;
import balancetalk.module.member.domain.Member;
import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {

    @Schema(description = "게시글을 작성한 회원 id", example = "1")
    private Long memberId;

    @Schema(description = "게시글 제목", example = "게시글 제목")
    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @Schema(description = "투료 종료 기한", example = "2024-03-16 08:27:17.391706\t")
    private LocalDateTime deadline;

    @Schema(description = "게시글 카테고리", example = "CASUAL")
    private PostCategory category;

    @Schema(description = "선택지 옵션 리스트", example = "[{\"title\": \"선택지 제목1\", \"description\": \"선택지 내용1\"}, {\"title\": \"선택지 제목2\", \"description\": \"선택지 내용2\"}]")
    private List<BalanceOptionDto> balanceOptions;

    @Schema(description = "태그 리스트", example = "[\"태그1\", \"태그2\", \"태그3\"]")
    private List<PostTagDto> tags;

    public Post toEntity(Member member) {
        return Post.builder()
                .member(member)
                .title(title)
                .deadline(deadline)
                .category(category)
                .options(balanceOptions.stream().map(BalanceOptionDto::toEntity).collect(Collectors.toList()))
                .postTags(tags.stream().map(PostTagDto::toEntity).collect(Collectors.toList()))
                .build();
    }
}
