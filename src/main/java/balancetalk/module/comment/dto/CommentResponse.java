package balancetalk.module.comment.dto;

import balancetalk.module.comment.domain.Comment;
import balancetalk.module.file.domain.File;
import balancetalk.module.member.domain.Member;
import balancetalk.module.vote.domain.Option;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Optional;

@Data
@AllArgsConstructor
@Builder
public class CommentResponse {

    @Schema(description = "댓글 id", example = "1")
    private Long id;

    @Schema(description = "해당 댓글에 맞는 게시글 id", example = "1")
    private Long postId;

    @Schema(description = "해당 댓글에 맞는 게시글 제목", example = "주먹밥 뭐 좋아함?")
    private String postTitle;

    @Schema(description = "댓글 작성자", example = "짱구")
    private String nickname;

    @Schema(description = "댓글 내용", example = "주먹밥은 역시 훈이 머리!")
    private String content;

    @Schema(description = "해당 댓글에 맞는 선택지 id", example = "A")
    private Option option;

//    @Schema(description = "댓글 블라인드 여부", example = "NORMAL")
//    private ViewStatus viewStatus;

    @Schema(description = "댓글 좋아요 개수", example = "24")
    private int likesCount;

//    @Schema(description = "댓글 신고 수", example = "3")
//    private long reportedCount;

    @Schema(description = "현재 사용자의 좋아요 여부", example = "true")
    private Boolean myLike;

    @Schema(description = "답글 수", example = "3")
    private int replyCount;

    @Schema(description = "댓글 생성 날짜")
    private LocalDateTime createdAt;

    @Schema(description = "댓글 수정 날짜")
    private LocalDateTime lastModifiedAt;




    public static CommentResponse fromEntity(Comment comment, Long balanceOptionId, boolean myLike) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .nickname(comment.getMember().getNickname())
                .postId(comment.getPost().getId())
                .option(Option.A) //TODO : 옵션 관리 방법에 따라 추후 재구현
                //.viewStatus(comment.getViewStatus())
                .likesCount(comment.getLikes().size())
                //.reportedCount(comment.reportedCount())
                .myLike(myLike)
                .createdAt(comment.getCreatedAt())
                .lastModifiedAt(comment.getLastModifiedAt())
                .build();
    }

    private static String getProfileImageUrl(Member member) {
        return Optional.ofNullable(member.getProfilePhoto())
                .map(File::getUrl)
                .orElse(null);
    }
}
