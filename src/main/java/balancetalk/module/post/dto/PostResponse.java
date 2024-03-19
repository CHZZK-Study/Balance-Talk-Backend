package balancetalk.module.post.dto;

import balancetalk.module.member.domain.Member;
import balancetalk.module.post.domain.BalanceOption;
import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
public class PostResponse {

    @Schema(description = "게시글 id", example = "1")
    private Long id;

    @Schema(description = "게시글 제목", example = "게시글 제목")
    private String title;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    @Schema(description = "투료 종료 기한", example = "2024-12-25T15:30:00")
    private LocalDateTime deadline;

    @Schema(description = "게시글 조회수", example = "126")
    private long views;

    @Schema(description = "게시글 추천수", example = "15")
    private long likesCount;

    @Schema(description = "추천 여부", example = "true")
    private boolean myLike;

    @Schema(description = "북마크 여부", example = "false")
    private boolean myBookmark;

    @Schema(description = "투표 여부", example = "true")
    private boolean myVote;

    @Schema(description = "투표한 선택지 id", example = "1")
    private Long selectedOptionId;

    @Schema(description = "게시글 카테고리", example = "CASUAL")
    private PostCategory category;

    @Schema(description = "선택지 옵션 리스트", example = "[{\"title\": \"선택지 제목1\", \"description\": \"선택지 내용1\" , \"storedFileName\": null}," +
            "{\"title\": \"선택지 제목2\", \"description\": \"선택지 내용2\", \"storedFileName\": null}]")
    private List<BalanceOptionDto> balanceOptions;

    @Schema(description = "태그 리스트", example = "[\"태그1\", \"태그2\", \"태그3\"]")
    private List<PostTagDto> postTags;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    @Schema(description = "게시글 작성일", example = "2023-12-25T15:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "게시글 작성자", example = "작성자 닉네임")
    private String createdBy;

    // todo: ProfilePhoto 추가
    public static PostResponse fromEntity(Post post, Member member, boolean myLike, boolean myBookmark, boolean myVote) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .deadline(post.getDeadline())
                .views(post.getViews())
                .likesCount(post.likesCount())
                .myLike(myLike)
                .myBookmark(myBookmark)
                .myVote(myVote)
                .selectedOptionId(getSelectedOptionId(post, member))
                .category(post.getCategory())
                .balanceOptions(getBalanceOptions(post))
                .postTags(getPostTags(post))
                .createdAt(post.getCreatedAt())
                .createdBy(post.getMember().getNickname())
                .build();
    }

    private static List<PostTagDto> getPostTags(Post post) {
        return post.getPostTags().stream()
                .map(PostTagDto::fromEntity)
                .collect(Collectors.toList());
    }

    private static List<BalanceOptionDto> getBalanceOptions(Post post) {
        return post.getOptions().stream()
                .map(BalanceOptionDto::fromEntity)
                .collect(Collectors.toList());
    }

    private static Long getSelectedOptionId(Post post, Member member) {
        return post.getOptions().stream()
                .filter(option -> Optional.ofNullable(option.getVotes())
                        .orElseGet(Collections::emptyList)
                        .stream()
                        .anyMatch(vote -> vote.getMember().getId().equals(member.getId())))
                .findFirst()
                .map(BalanceOption::getId)
                .orElse(null);
    }
}
