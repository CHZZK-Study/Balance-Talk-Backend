package balancetalk.comment.dto;

import balancetalk.comment.domain.Comment;
import balancetalk.member.domain.Member;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.vote.domain.VoteOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

public class CommentDto {
    @Data
    @Builder
    @AllArgsConstructor
    public static class Request {

        @Schema(description = "댓글 내용", example = "댓글 내용...")
        private String content;

        @Schema(description = "선택지", example = "A")
        private VoteOption option;

        public Comment toEntity(Member member, TalkPick talkPick) {
            return Comment.builder()
                    .content(content)
                    .member(member)
                    .talkPick(talkPick)
                    .selectedOption(option) // TODO : Vote 구현 완료 후 member와 talkPick 이용해서 선택한 option 가져오기
                    .build();
        }
    }
    @Data
    @AllArgsConstructor
    @Builder
    public static class Response {

        @Schema(description = "댓글 id", example = "1")
        private Long id;

        @Schema(description = "해당 댓글에 맞는 게시글 id", example = "1")
        private Long talkPickId;

        @Schema(description = "해당 댓글에 맞는 게시글 제목", example = "주먹밥 뭐 좋아함?")
        private String talkPickTitle;

        @Schema(description = "댓글 작성자", example = "짱구")
        private String nickname;

        @Schema(description = "댓글 내용", example = "주먹밥은 역시 훈이 머리!")
        private String content;

        @Schema(description = "해당 댓글에 맞는 선택지 id", example = "A")
        private VoteOption option;

        @Schema(description = "댓글 좋아요 개수", example = "24")
        private int likesCount;

        @Schema(description = "현재 사용자의 좋아요 여부", example = "true")
        private Boolean myLike;

        @Schema(description = "답글 수", example = "3")
        private int replyCount;

        @Schema(description = "댓글 생성 날짜")
        private LocalDateTime createdAt;

        @Schema(description = "댓글 수정 날짜")
        private LocalDateTime lastModifiedAt;

        public static Response fromEntity(Comment comment, boolean myLike) {
            return Response.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .nickname(comment.getMember().getNickname())
                    .talkPickId(comment.getTalkPick().getId())
                    .option(comment.getSelectedOption())
                    //.likesCount(comment.getLikes().size()) TODO : 좋아요 구현 시 작성
                    .myLike(myLike)
                    //.replyCount(comment.getReplies().size())
                    .createdAt(comment.getCreatedAt())
                    .lastModifiedAt(comment.getLastModifiedAt())
                    .build();
        }
    }
}
