package balancetalk.comment.application;

import balancetalk.comment.domain.Comment;
import balancetalk.comment.domain.CommentRepository;
import balancetalk.comment.dto.CommentDto;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.global.notification.application.NotificationService;
import balancetalk.global.notification.domain.NotificationTitleCategory;
import balancetalk.like.domain.LikeRepository;
import balancetalk.like.domain.LikeType;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.member.dto.GuestOrApiMember;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.repository.TalkPickRepository;
import balancetalk.vote.domain.VoteOption;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static balancetalk.global.exception.ErrorCode.*;
import static balancetalk.global.notification.domain.NotificationMessage.COMMENT_REPLY_100;
import static balancetalk.global.notification.domain.NotificationMessage.COMMENT_REPLY_50;
import static balancetalk.global.notification.domain.NotificationMessage.FIRST_COMMENT_REPLY;
import static balancetalk.global.notification.domain.NotificationMessage.TALK_PICK_COMMENT;
import static balancetalk.global.notification.domain.NotificationMessage.TALK_PICK_COMMENT_100;
import static balancetalk.global.notification.domain.NotificationMessage.TALK_PICK_COMMENT_1000;
import static balancetalk.global.notification.domain.NotificationStandard.FIRST_STANDARD_OF_NOTIFICATION;
import static balancetalk.global.notification.domain.NotificationStandard.FOURTH_STANDARD_OF_NOTIFICATION;
import static balancetalk.global.notification.domain.NotificationStandard.SECOND_STANDARD_OF_NOTIFICATION;
import static balancetalk.global.notification.domain.NotificationStandard.THIRD_STANDARD_OF_NOTIFICATION;
import static balancetalk.global.notification.domain.NotificationTitleCategory.OTHERS_TALK_PICK;
import static balancetalk.global.notification.domain.NotificationTitleCategory.WRITTEN_TALK_PICK;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private static final int MIN_COUNT_FOR_BEST_COMMENT = 10;

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final TalkPickRepository talkPickRepository;
    private final LikeRepository likeRepository;
    private final NotificationService notificationService;

    @Value("${comments.max-depth}")
    private int maxDepth;

    public void createComment(@Valid CommentDto.CreateCommentRequest createCommentRequest, Long talkPickId, ApiMember apiMember) {
        //TODO : Vote 기능 구현 완료 후 추가 예외 처리 필요
        Member member = apiMember.toMember(memberRepository);
        TalkPick talkPick = validateTalkPickId(talkPickId);

        // option이 VoteOption에 존재하는 값인지 확인 및 예외 처리
        VoteOption option = createCommentRequest.getOption();
        if (option == null || !EnumSet.allOf(VoteOption.class).contains(option)) {
            throw new BalanceTalkException(NOT_FOUND_VOTE_OPTION);
        }

        Comment comment = createCommentRequest.toEntity(member, talkPick);
        commentRepository.save(comment);
        sendCommentNotification(talkPick);
    }

    @Transactional
    public void createCommentReply(CommentDto.CreateCommentRequest createCommentRequest, Long talkPickId, Long commentId,
                                   ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        TalkPick talkPick = validateTalkPickId(talkPickId);
        Comment parentComment = validateCommentId(commentId);

        // 부모 댓글과 연결된 게시글이 아닌 경우 예외 처리
        if (!parentComment.getTalkPick().equals(talkPick)) {
            throw new BalanceTalkException(NOT_FOUND_PARENT_COMMENT_AT_THAT_TALK_PICK);
        }

        // 부모 댓글의 depth가 maxDepth를 초과하는 경우 예외 처리 (답글에 답글 불가)
        validateDepth(parentComment);

        Comment commentReply = createCommentRequest.toEntity(member, talkPick, parentComment);
        commentRepository.save(commentReply);

        // 알림 전송

        sendReplyNotification(parentComment);
    }

    @Transactional(readOnly = true)
    public Page<CommentDto.CommentResponse> findAllComments(Long talkPickId, Pageable pageable,
                                                            GuestOrApiMember guestOrApiMember) {
        validateTalkPickId(talkPickId);

        Page<Comment> comments = commentRepository.findAllByTalkPickId(talkPickId, pageable);

        return comments.map(comment -> {
            int likesCount = likeRepository.countByResourceIdAndLikeType(comment.getId(), LikeType.COMMENT);
            boolean myLike = isCommentMyLiked(comment.getId(), guestOrApiMember);
            return CommentDto.CommentResponse.fromEntity(comment, likesCount, myLike);
        });
    }

    @Transactional(readOnly = true)
    public Page<CommentDto.CommentResponse> findAllBestComments(Long talkPickId, Pageable pageable, GuestOrApiMember guestOrApiMember) {
        validateTalkPickId(talkPickId);

        List<Comment> allComments = commentRepository.findByTalkPickIdOrderByLikesCountDescCreatedAtDesc(talkPickId, LikeType.COMMENT);
        List<CommentDto.CommentResponse> bestComments = new ArrayList<>();
        List<CommentDto.CommentResponse> otherComments = new ArrayList<>();

        // 최대 좋아요 수 구하기
        int maxLikes = allComments.stream()
                .mapToInt(comment -> likeRepository.countByResourceIdAndLikeType(comment.getId(), LikeType.COMMENT))
                .max()
                .orElse(0);

        // 좋아요 10개 이상인 댓글이 있는 경우
        if (maxLikes >= MIN_COUNT_FOR_BEST_COMMENT) {
            for (Comment comment : allComments) {
                boolean myLike = isCommentMyLiked(comment.getId(), guestOrApiMember);
                int likeCount = likeRepository.countByResourceIdAndLikeType(comment.getId(), LikeType.COMMENT);
                comment.setIsBest(likeCount >= MIN_COUNT_FOR_BEST_COMMENT);
                CommentDto.CommentResponse response = CommentDto.CommentResponse.fromEntity(comment, likeCount, myLike);
                if (comment.getIsBest()) {
                    bestComments.add(response);
                } else {
                    otherComments.add(response);
                }
            }
        } else {
            for (Comment comment : allComments) {
                boolean myLike = isCommentMyLiked(comment.getId(), guestOrApiMember);
                int likeCount = likeRepository.countByResourceIdAndLikeType(comment.getId(), LikeType.COMMENT);
                comment.setIsBest(likeCount == maxLikes);
                CommentDto.CommentResponse response = CommentDto.CommentResponse.fromEntity(comment, likeCount, myLike);
                if (comment.getIsBest()) {
                    bestComments.add(response);
                } else {
                    otherComments.add(response);
                }
            }
        }

        bestComments.sort(Comparator.comparing(CommentDto.CommentResponse::getCreatedAt).reversed());
        otherComments.sort(Comparator.comparing(CommentDto.CommentResponse::getCreatedAt).reversed());

        List<CommentDto.CommentResponse> result = new ArrayList<>();
        result.addAll(bestComments);
        result.addAll(otherComments);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), result.size());

        return new PageImpl<>(result.subList(start, end), pageable, result.size());
    }

    public void updateComment(Long commentId, Long talkPickId, String content, ApiMember apiMember) {
        Comment comment = validateCommentByMemberAndTalkPick(commentId, talkPickId, apiMember, FORBIDDEN_COMMENT_MODIFY);
        comment.updateContent(content);
    }

    public void deleteComment(Long commentId, Long talkPickId, ApiMember apiMember) {
        validateCommentByMemberAndTalkPick(commentId, talkPickId, apiMember, FORBIDDEN_COMMENT_DELETE);
        commentRepository.deleteById(commentId);
    }

    private Comment validateCommentByMemberAndTalkPick(Long commentId, Long talkPickId, ApiMember apiMember, ErrorCode errorCode) {
        Member member = apiMember.toMember(memberRepository);
        Comment comment = validateCommentId(commentId);
        validateTalkPickId(talkPickId);

        if (!member.equals(comment.getMember())) {
            throw new BalanceTalkException(errorCode);
        }

        if (!comment.getTalkPick().getId().equals(talkPickId)) {
            throw new BalanceTalkException(NOT_FOUND_COMMENT_AT_THAT_TALK_PICK);
        }

        return comment;
    }

    private TalkPick validateTalkPickId(Long talkPickId) {
        return talkPickRepository.findById(talkPickId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_TALK_PICK));
    }

    private Comment validateCommentId(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_COMMENT));
    }

    private boolean isCommentMyLiked(Long commentId, GuestOrApiMember guestOrApiMember) {
        if (guestOrApiMember.isGuest()) {
            return false;
        }

        Long memberId = guestOrApiMember.toMember(memberRepository).getId();

        return likeRepository.existsByResourceIdAndMemberId(commentId, memberId);
    }

    private void validateDepth(Comment parentComment) {
        int depth = calculateDepth(parentComment);
        if (depth >= maxDepth) {
            throw new BalanceTalkException(EXCEED_MAX_DEPTH);
        }
    }

    private int calculateDepth(Comment comment) {
        int depth = 0;
        while (comment.getParent() != null) {
            depth++;
            comment = comment.getParent();
        }
        return depth;
    }

    private void sendReplyNotification(Comment parentComment) {
        long replyCount = parentComment.getReplies().size();
        Member parentCommentAuthor = parentComment.getMember();
        TalkPick talkPick = parentComment.getTalkPick();
        String replyCountKey = "REPLY_" + replyCount;
        String firstReplyKey = "FIRST_REPLY";
        Map<String, Boolean> notificationHistory = parentComment.getNotificationHistory();
        String category = OTHERS_TALK_PICK.getCategory();

        // 모든 답글 중 원래 댓글 작성자가 아닌 다른 사용자가 처음으로 답글을 달았는지 확인
        boolean isFirstReplyFromOther = parentComment.getReplies().stream()
                .anyMatch(reply -> !reply.getMember().equals(parentCommentAuthor));

        if (parentCommentAuthor.equals(talkPick.getMember())) {
            category = WRITTEN_TALK_PICK.getCategory();
        }

        // 첫 답글 알림
        if ((isFirstReplyFromOther && !parentComment.getIsNotifiedForFirstReply()) && !notificationHistory.getOrDefault(firstReplyKey, false)) {
            notificationService.sendTalkPickNotification(parentCommentAuthor,talkPick,
                    category, FIRST_COMMENT_REPLY.getMessage());
            parentComment.setIsNotifiedForFirstReplyTrue();
            // 50, 100개 답글 알림
        } else if (replyCount == SECOND_STANDARD_OF_NOTIFICATION.getCount() && !notificationHistory.getOrDefault(replyCountKey, false)) {
            notificationService.sendTalkPickNotification(parentCommentAuthor, talkPick, category,
                    COMMENT_REPLY_50.getMessage());
        } else if (replyCount == THIRD_STANDARD_OF_NOTIFICATION.getCount() && !notificationHistory.getOrDefault(replyCountKey, false)) {
            notificationService.sendTalkPickNotification(parentCommentAuthor, talkPick, category,
                    COMMENT_REPLY_100.getMessage());
        }
        notificationHistory.put(firstReplyKey, true);
        parentComment.setNotificationHistory(notificationHistory);
    }

    private void sendCommentNotification(TalkPick talkPick) {
        long commentCount = talkPick.getComments().size();
        Member member = talkPick.getMember();
        String commentCountKey = "COMMENT_" + commentCount;
        Map<String, Boolean> notificationHistory = talkPick.getNotificationHistory();
        String category = WRITTEN_TALK_PICK.getCategory();

        boolean isMilestoneCommented = (commentCount == FIRST_STANDARD_OF_NOTIFICATION.getCount() ||
                commentCount == SECOND_STANDARD_OF_NOTIFICATION.getCount() ||
                commentCount == THIRD_STANDARD_OF_NOTIFICATION.getCount() ||
                (commentCount > THIRD_STANDARD_OF_NOTIFICATION.getCount() &&
                        commentCount % THIRD_STANDARD_OF_NOTIFICATION.getCount() == 0) ||
                (commentCount > FOURTH_STANDARD_OF_NOTIFICATION.getCount() &&
                        commentCount % FOURTH_STANDARD_OF_NOTIFICATION.getCount() == 0));

        // 댓글 개수가 10, 50, 100*n개, 1000*n개 일 때 알림
        if (isMilestoneCommented && !notificationHistory.getOrDefault(commentCountKey, false)) {
            notificationService.sendTalkPickNotification(member, talkPick, category, TALK_PICK_COMMENT.format(commentCount));
            // 댓글 개수가 100개일 때 배찌 획득 알림
            if (commentCount == THIRD_STANDARD_OF_NOTIFICATION.getCount()) {
                notificationService.sendTalkPickNotification(member, talkPick, category, TALK_PICK_COMMENT_100.getMessage());
            }
            // 댓글 개수가 1000개일 때 배찌 획득 알림
            else if (commentCount == FOURTH_STANDARD_OF_NOTIFICATION.getCount()) {
                notificationService.sendTalkPickNotification(member, talkPick, category, TALK_PICK_COMMENT_1000.getMessage());
            }
            notificationHistory.put(commentCountKey, true);
            talkPick.setNotificationHistory(notificationHistory);
        }
    }
}
