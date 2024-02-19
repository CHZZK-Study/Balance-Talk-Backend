package balancetalk.module.post.dto;

import balancetalk.module.member.domain.Member;
import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {

    private Long memberId;
    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime deadline;

    private PostCategory category;
    private List<BalanceOptionDto> balanceOptions;
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
