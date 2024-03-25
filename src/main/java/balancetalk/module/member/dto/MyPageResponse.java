package balancetalk.module.member.dto;


import balancetalk.module.bookmark.domain.Bookmark;
import balancetalk.module.comment.domain.Comment;
import balancetalk.module.post.domain.Post;
import balancetalk.module.vote.domain.Vote;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyPageResponse {
    @Schema(description = "게시글 제목", example = "게시글 제목")
    private String postTitle;

    @Schema(description = "선택지 제목", example = "선택지 제목")
    private String balanceOptionTitle;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    @Schema(description = "게시글 작성일", example = "2023-12-25T15:30:00")
    private LocalDateTime postCreatedAt;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    @Schema(description = "북마크 추가 날짜", example = "2023-12-25T15:30:00")
    private LocalDateTime bookmarkedAt;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    @Schema(description = "투표 날짜", example = "2023-12-25T15:30:00")
    private LocalDateTime votedAt;

    @Schema(description = "댓글 내용", example = "댓글 내용...")
    private String commentContent;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    @Schema(description = "댓글 작성 날짜", example = "2023-12-25T15:30:00")
    private LocalDateTime commentCreatedAt;

    public static MyPageResponse fromEntity(Post post) {
        return MyPageResponse.builder()
                .postTitle(post.getTitle())
                .postCreatedAt(post.getCreatedAt())
                .build();
    }

    public static MyPageResponse fromEntity(Bookmark bookmark) {
        return MyPageResponse.builder()
                .postTitle(bookmark.getPost().getTitle())
                .bookmarkedAt(bookmark.getCreatedAt())
                .build();
    }

    public static MyPageResponse fromEntity(Vote vote, Post post) {
        return MyPageResponse.builder()
                .balanceOptionTitle(vote.getBalanceOption().getTitle())
                .votedAt(vote.getCreatedAt())
                .postTitle(post.getTitle())
                .build();
    }

    public static MyPageResponse fromEntity(Comment comment) {
        return MyPageResponse.builder()
                .commentContent(comment.getContent())
                .postTitle(comment.getPost().getTitle())
                .commentCreatedAt(comment.getCreatedAt())
                .build();
    }
}
