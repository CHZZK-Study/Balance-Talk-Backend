package balancetalk.module.post.dto;

import balancetalk.module.file.domain.File;
import balancetalk.module.member.domain.Member;
import balancetalk.module.post.domain.BalanceOption;
import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostCategory;
import balancetalk.module.post.domain.PostTag;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.stream.IntStream;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {

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

    public Post toEntity(Member member, List<File> images) {
        return Post.builder()
                .member(member)
                .title(title)
                .deadline(deadline)
                .category(category)
                .options(getBalanceOptions(images))
                .postTags(getPostTags())
                .build();
    }

    private List<BalanceOption> getBalanceOptions(List<File> images) {
        return IntStream.range(0, balanceOptions.size())
                .mapToObj(i -> balanceOptions.get(i).toEntity(images.get(i)))
                .collect(Collectors.toList());
    }

    private List<PostTag> getPostTags() {
        return tags.stream().map(PostTagDto::toEntity).collect(Collectors.toList());
    }
}
