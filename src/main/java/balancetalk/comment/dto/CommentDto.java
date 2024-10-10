package balancetalk.comment.dto;

import balancetalk.comment.domain.Comment;
import balancetalk.member.domain.Member;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.ViewStatus;
import balancetalk.vote.domain.VoteOption;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class CommentDto {
    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "댓글 생성 요청")
    public static class CreateCommentRequest {

        @Schema(description = "댓글 내용", example = "댓글 내용...")
        private String content;

        @Schema(description = "선택지", example = "A")
        private VoteOption option;

        @Schema(description = "부모 댓글 id (답글 작성이 아닐 경우, 작성 x)", example = "5")
        private Long parentId;

        public Comment toEntity(Member member, TalkPick talkPick) {
            return Comment.builder()
                    .content(content)
                    .member(member)
                    .talkPick(talkPick)
                    .voteOption(option) // TODO : Vote 구현 완료 후 member와 talkPick 이용해서 선택한 option 가져오기
                    .isBest(false)
                    .viewStatus(ViewStatus.NORMAL)
                    .reportedCount(0)
                    .isNotifiedForFirstReply(false)
                    .editedAt(LocalDateTime.now())
                    .isEdited(false)
                    .build();
        }

        public Comment toEntity(Member member, TalkPick talkPick, Comment parent) {
            return Comment.builder()
                    .content(content)
                    .member(member)
                    .talkPick(talkPick)
                    .voteOption(option) // TODO : Vote 구현 완료 후 member와 talkPick 이용해서 선택한 option 가져오기
                    .isBest(false)
                    .viewStatus(ViewStatus.NORMAL)
                    .reportedCount(0)
                    .parent(parent)
                    .isEdited(false)
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "댓글 수정 요청")
    public static class UpdateCommentRequest {

        @Schema(description = "수정할 댓글 내용", example = "댓글 내용...")
        private String content;
    }


    @Data
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "최신순 댓글 조회 응답")
    public static class CommentOrderByCreatedAtResponse {

        @Schema(description = "댓글 id", example = "1")
        private Long id;

        @Schema(description = "해당 댓글에 맞는 톡픽 id", example = "1")
        private Long talkPickId;

        @Schema(description = "해당 댓글에 맞는 톡픽 제목", example = "메시 vs 호날두")
        private String talkPickTitle;

        @Schema(description = "댓글 작성자", example = "운영자1")
        private String nickname;

        @Schema(description = "댓글 작성자 프로필 이미지", example = "https://balancetalk.com/profile/1")
        private String profileImage;

        @Schema(description = "댓글 내용", example = "너는나를존중해야한다나는발롱도르5개와수많은개인트로피를들어올렸으며"
                + "2016유로에서포르투갈을이끌고우승을차지했고동시에A매치역대최다득점자이다")
        private String content;

        @Schema(description = "해당 댓글에 맞는 선택지 이름", example = "A")
        private VoteOption option;

        @Schema(description = "댓글 좋아요 개수", example = "24")
        private int likesCount;

        @Schema(description = "현재 사용자의 좋아요 여부", example = "true")
        private Boolean myLike;

        @Schema(description = "부모 댓글 id (답글이 아닐 경우, null 반환)", example = "5")
        private Long parentId;

        @Schema(description = "답글 수", example = "3")
        private int replyCount;

        @Schema(description = "댓글 수정 여부", example = "false")
        private boolean isEdited;

        @Schema(description = "댓글이 신고당한 횟수", example = "0")
        private int reportedCount;

        @Schema(description = "댓글 생성 날짜")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd hh:mm")
        private LocalDateTime createdAt;

        @Schema(description = "댓글 수정 날짜")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd hh:mm")
        private LocalDateTime lastModifiedAt;

        public static CommentOrderByCreatedAtResponse fromEntity(Comment comment, int likesCount, boolean myLike) {
            return CommentOrderByCreatedAtResponse.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .nickname(comment.getMember().getNickname())
                    .profileImage(comment.getMember().getProfileImgUrl())
                    .talkPickId(comment.getTalkPick().getId())
                    .talkPickTitle(comment.getTalkPick().getTitle())
                    .option(comment.getVoteOption())
                    .likesCount(likesCount)
                    .myLike(myLike)
                    .parentId(comment.getParent() == null ? null : comment.getParent().getId())
                    .replyCount(comment.getReplies() == null ? 0 : comment.getReplies().size())
                    .reportedCount(comment.getReportedCount())
                    .isEdited(comment.isEdited())
                    .createdAt(comment.getCreatedAt())
                    .lastModifiedAt(comment.getLastModifiedAt())
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "베스트순 댓글 조회 응답")
    public static class CommentOrderByBestResponse {

        @Schema(description = "댓글 id", example = "1")
        private Long id;

        @Schema(description = "해당 댓글에 맞는 톡픽 id", example = "1")
        private Long talkPickId;

        @Schema(description = "해당 댓글에 맞는 톡픽 제목", example = "메시 vs 호날두")
        private String talkPickTitle;

        @Schema(description = "댓글 작성자", example = "운영자1")
        private String nickname;

        @Schema(description = "댓글 작성자 프로필 이미지", example = "https://balancetalk.com/profile/1")
        private String profileImage;

        @Schema(description = "댓글 내용", example = "너는나를존중해야한다나는발롱도르5개와수많은개인트로피를들어올렸으며"
                + "2016유로에서포르투갈을이끌고우승을차지했고동시에A매치역대최다득점자이다")
        private String content;

        @Schema(description = "해당 댓글에 맞는 선택지 이름", example = "A")
        private VoteOption option;

        @Schema(description = "댓글 좋아요 개수", example = "24")
        private int likesCount;

        @Schema(description = "현재 사용자의 좋아요 여부", example = "true")
        private Boolean myLike;

        @Schema(description = "부모 댓글 id (답글이 아닐 경우, null 반환)", example = "5")
        private Long parentId;

        @Schema(description = "답글 수", example = "3")
        private int replyCount;

        @Schema(description = "베스트 댓글 여부", example = "true")
        private boolean isBest;

        @Schema(description = "댓글 수정 여부", example = "false")
        private boolean isEdited;

        @Schema(description = "댓글이 신고당한 횟수", example = "0")
        private int reportedCount;

        @Schema(description = "댓글 생성 날짜")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd hh:mm")
        private LocalDateTime createdAt;

        @Schema(description = "댓글 수정 날짜")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd hh:mm")
        private LocalDateTime lastModifiedAt;

        public static CommentOrderByBestResponse fromEntity(Comment comment, int likesCount, boolean myLike) {
            return CommentOrderByBestResponse.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .nickname(comment.getMember().getNickname())
                    .profileImage(comment.getMember().getProfileImgUrl())
                    .talkPickId(comment.getTalkPick().getId())
                    .talkPickTitle(comment.getTalkPick().getTitle())
                    .option(comment.getVoteOption())
                    .likesCount(likesCount)
                    .myLike(myLike)
                    .parentId(comment.getParent() == null ? null : comment.getParent().getId())
                    .replyCount(comment.getReplies() == null ? 0 : comment.getReplies().size())
                    .reportedCount(comment.getReportedCount())
                    .isBest(comment.getIsBest())
                    .isEdited(comment.isEdited())
                    .createdAt(comment.getCreatedAt())
                    .lastModifiedAt(comment.getLastModifiedAt())
                    .build();
        }
    }
}
