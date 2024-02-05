package balancetalk.module.post.dto;

import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostCategory;
import balancetalk.module.post.domain.PostTag;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostRequestDto {

    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime deadline;
    private Long views;
    private PostCategory Category;
    private List<BalanceOptionDto> balanceOptions;
    private List<PostTagDto> tags;

    public Post toEntity() {
        return Post.builder()
                .title(title)
                .deadline(deadline)
                .views(views)
                .category(getCategory())
                .options(balanceOptions.stream().map(BalanceOptionDto::toEntity).collect(Collectors.toList()))
                .postTags(tags.stream().map(PostTagDto::toEntity).collect(Collectors.toList()))
                .build();
    }
}
