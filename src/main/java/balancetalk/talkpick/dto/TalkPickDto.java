package balancetalk.talkpick.dto;

import balancetalk.bookmark.domain.TalkPickBookmark;
import balancetalk.comment.domain.Comment;
import balancetalk.member.domain.Member;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.vote.domain.TalkPickVote;
import balancetalk.vote.domain.VoteOption;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static balancetalk.talkpick.domain.ViewStatus.NORMAL;
import static balancetalk.vote.domain.VoteOption.A;
import static balancetalk.vote.domain.VoteOption.B;

public class TalkPickDto {

    @Schema(description = "톡픽 생성/수정 요청")
    @Data
    @AllArgsConstructor
    public static class CreateOrUpdateTalkPickRequest {

        @Schema(description = "제목", example = "제목")
        @NotBlank(message = "제목은 공백을 허용하지 않습니다.")
        @Size(max = 50, message = "제목은 50자 이하여야 합니다.")
        private String title;

        @Schema(description = "본문 내용", example = "본문 내용")
        @NotBlank(message = "본문 내용은 공백을 허용하지 않습니다.")
        private String content;

        @Schema(description = "선택지 A 이름", example = "선택지 A 이름")
        @NotBlank(message = "선택지 이름은 공백을 허용하지 않습니다.")
        @Size(max = 10, message = "선택지 이름은 10자 이하여야 합니다.")
        private String optionA;

        @Schema(description = "선택지 B 이름", example = "선택지 B 이름")
        @NotBlank(message = "선택지 이름은 공백을 허용하지 않습니다.")
        @Size(max = 10, message = "선택지 이름은 10자 이하여야 합니다.")
        private String optionB;

        @Schema(description = "출처 URL", example = "https://github.com/CHZZK-Study/Balance-Talk-Backend/issues/506")
        private String sourceUrl;

        @Schema(description = "첨부한 이미지 고유 이름 목록",
                example = "[" +
                        "\"9b4856fe-b624-4e54-ad80-a94e083301d2_czz.png\",\n" +
                        "\"fdcbd97b-f9be-45d1-b855-43f3fd17d5a6_6d588490-d5d4-4e47-b5d0-957e6ed4830b_prom.jpeg\"" +
                        "]")
        private List<String> storedNames;

        public TalkPick toEntity(Member member) {
            return TalkPick.builder()
                    .member(member)
                    .title(title)
                    .content(content)
                    .optionA(optionA)
                    .optionB(optionB)
                    .sourceUrl(sourceUrl)
                    .views(0L)
                    .bookmarks(0L)
                    .viewStatus(NORMAL)
                    .editedAt(LocalDateTime.now())
                    .build();
        }
    }

    @Schema(description = "톡픽 상세 조회 응답")
    @Data
    @AllArgsConstructor
    @Builder
    public static class TalkPickDetailResponse {

        @Schema(description = "톡픽 ID", example = "톡픽 ID")
        private long id;

        @Schema(description = "제목", example = "톡픽 제목")
        private String title;

        @Schema(description = "본문 내용", example = "톡픽 본문 내용")
        private String content;

        private SummaryResponse summary;

        @Schema(description = "선택지 A 이름", example = "선택지 A 이름")
        private String optionA;

        @Schema(description = "선택지 B 이름", example = "선택지 B 이름")
        private String optionB;

        @Schema(description = "출처 URL", example = "https://github.com/CHZZK-Study/Balance-Talk-Backend/issues/506")
        private String sourceUrl;

        @Schema(description = "톡픽 작성 시 첨부한 이미지 URL 목록",
                example = "[" +
                        "\"https://picko-image.s3.ap-northeast-2.amazonaws.com/temp-talk-pick/9b4856fe-b624-4e54-ad80-a94e083301d2_czz.png\",\n" +
                        "\"https://picko-image.s3.ap-northeast-2.amazonaws.com/temp-talk-pick/fdcbd97b-f9be-45d1-b855-43f3fd17d5a6_6d588490-d5d4-4e47-b5d0-957e6ed4830b_prom.jpeg\"" +
                        "]")
        private List<String> imgUrls;

        @Schema(description = "톡픽 작성 시 첨부한 이미지 고유 이름",
                example = "[" +
                        "\"9b4856fe-b624-4e54-ad80-a94e083301d2_czz.png\",\n" +
                        "\"fdcbd97b-f9be-45d1-b855-43f3fd17d5a6_6d588490-d5d4-4e47-b5d0-957e6ed4830b_prom.jpeg\"" +
                        "]")
        private List<String> imgStoredNames;

        @Schema(description = "선택지 A 투표수", example = "12")
        private long votesCountOfOptionA;

        @Schema(description = "선택지 B 투표수", example = "12")
        private long votesCountOfOptionB;

        @Schema(description = "조회수", example = "152")
        private long views;

        @Schema(description = "저장수", example = "143")
        private long bookmarks;

        @Schema(description = "북마크 여부", example = "true")
        private Boolean myBookmark;

        @Schema(description = "투표한 선택지", example = "A")
        private VoteOption votedOption;

        @Schema(description = "작성자 닉네임", example = "hj30")
        private String writer;

        @Schema(description = "작성일", example = "2024-08-04")
        private LocalDate createdAt;

        @Schema(description = "수정 여부", example = "true")
        private Boolean isUpdated;

        public static TalkPickDetailResponse from(TalkPick entity,
                                                  List<String> imgUrls,
                                                  List<String> imgStoredNames,
                                                  boolean myBookmark,
                                                  VoteOption votedOption) {
            return TalkPickDetailResponse.builder()
                    .id(entity.getId())
                    .title(entity.getTitle())
                    .content(entity.getContent())
                    .summary(new SummaryResponse(entity.getSummary()))
                    .optionA(entity.getOptionA())
                    .optionB(entity.getOptionB())
                    .sourceUrl(entity.getSourceUrl())
                    .imgUrls(imgUrls)
                    .imgStoredNames(imgStoredNames)
                    .votesCountOfOptionA(entity.votesCountOf(A))
                    .votesCountOfOptionB(entity.votesCountOf(B))
                    .views(entity.getViews())
                    .bookmarks(entity.getBookmarks())
                    .myBookmark(myBookmark)
                    .votedOption(votedOption)
                    .writer(entity.getWriterNickname())
                    .createdAt(entity.getCreatedAt().toLocalDate())
                    .isUpdated(entity.isEdited())
                    .build();
        }
    }

    @Schema(description = "톡픽 목록 조회 응답")
    @Data
    @AllArgsConstructor
    @Builder
    public static class TalkPickResponse {

        @Schema(description = "톡픽 ID", example = "톡픽 ID")
        private long id;

        @Schema(description = "제목", example = "톡픽 제목")
        private String title;

        @Schema(description = "작성자 닉네임", example = "hj30")
        private String writer;

        @Schema(description = "작성일", example = "2024-08-04")
        private LocalDate createdAt;

        @Schema(description = "조회수", example = "152")
        private long views;

        @Schema(description = "저장수", example = "143")
        private long bookmarks;

        @QueryProjection
        public TalkPickResponse(Long id, String title, String writer, LocalDateTime createdAt, long views, long bookmarks) {
            this.id = id;
            this.title = title;
            this.writer = writer;
            this.createdAt = createdAt.toLocalDate();
            this.views = views;
            this.bookmarks = bookmarks;
        }
    }

    @Schema(description = "마이페이지 톡픽 응답")
    @Data
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TalkPickMyPageResponse {

        @Schema(description = "톡픽 ID", example = "1")
        private long id;

        @Schema(description = "제목", example = "톡픽 제목")
        private String title;

        @Schema(description = "투표한 선택지", example = "A")
        private VoteOption voteOption;

        @Schema(description = "댓글 내용", example = "댓글 내용")
        private String commentContent;

        @Schema(description = "북마크 된 개수", example = "12")
        private long bookmarks;

        @Schema(description = "북마크 여부")
        private boolean isBookmarked;

        @Schema(description = "댓글 개수", example = "2")
        private long commentCount;

        @Schema(description = "최종 수정일(마이페이지 등록 날짜)")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
        private LocalDateTime editedAt;

        /*
        @Schema(description = "선택지 A 이미지", example = "https://pikko-image.s3.ap-northeast-2.amazonaws.com/balance-game/067cc56e-21b7-468f-a2c1-4839036ee7cd_unnamed.png")
        private String optionAImg;

        @Schema(description = "선택지 B 이미지", example = "https://pikko-image.s3.ap-northeast-2.amazonaws.com/balance-game/1157461e-a685-42fd-837e-7ed490894ca6_unnamed.png")
        private String optionBImg;

         */ // TODO : 톡픽 선택지 이미지 저장 구현 시 완성 가능

        public static TalkPickMyPageResponse from(TalkPick talkPick, TalkPickBookmark talkPickBookmark) {
            return TalkPickMyPageResponse.builder()
                    .id(talkPick.getId())
                    .title(talkPick.getTitle())
                    .editedAt(talkPick.getEditedAt())
                    .isBookmarked(talkPickBookmark.isActive())
                    .bookmarks(talkPick.getBookmarks())
                    .commentCount(!talkPick.getComments().isEmpty() ? talkPick.getComments().size() : 0)
                    .editedAt(talkPick.getEditedAt())
                    .build();
        }

        public static TalkPickMyPageResponse from(TalkPick talkPick, TalkPickVote vote) {
            return TalkPickMyPageResponse.builder()
                    .id(talkPick.getId())
                    .title(talkPick.getTitle())
                    .voteOption(vote.getVoteOption())
                    .bookmarks(talkPick.getBookmarks())
                    .commentCount(!talkPick.getComments().isEmpty() ? talkPick.getComments().size() : 0)
                    .editedAt(talkPick.getEditedAt())
                    .build();
        }

        public static TalkPickMyPageResponse from(TalkPick talkPick, Comment comment) {
            return TalkPickMyPageResponse.builder()
                    .id(talkPick.getId())
                    .title(talkPick.getTitle())
                    .commentContent(comment.getContent())
                    .bookmarks(talkPick.getBookmarks())
                    .commentCount(!talkPick.getComments().isEmpty() ? talkPick.getComments().size() : 0)
                    .editedAt(talkPick.getEditedAt())
                    .build();
        }

        public static TalkPickMyPageResponse fromMyTalkPick(TalkPick talkPick) {
            return TalkPickMyPageResponse.builder()
                    .id(talkPick.getId())
                    .title(talkPick.getTitle())
                    .bookmarks(talkPick.getBookmarks())
                    .commentCount(!talkPick.getComments().isEmpty() ? talkPick.getComments().size() : 0)
                    .editedAt(talkPick.getEditedAt())
                    .build();
        }
    }
}