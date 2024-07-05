package balancetalk.comment.application;

import balancetalk.comment.domain.Comment;
import balancetalk.comment.domain.CommentRepository;
import balancetalk.comment.dto.CommentDto;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
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
import static balancetalk.global.utils.SecurityUtils.getCurrentMember;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private static final int MIN_COUNT_FOR_BEST_COMMENT = 10;

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final TalkPickRepository talkPickRepository;

    @Value("${comments.max-depth}")
    private int maxDepth;

    public void createComment(@Valid CommentDto.CreateCommentRequest createCommentRequest, Long talkPickId) {
        //TODO : Vote 기능 구현 완료 후 추가 예외 처리 필요
        Member member = getCurrentMember(memberRepository);
        TalkPick talkPick = validateTalkPickId(talkPickId);

        // option이 VoteOption에 존재하는 값인지 확인 및 예외 처리
        VoteOption option = createCommentRequest.getOption();
        if (option == null || !EnumSet.allOf(VoteOption.class).contains(option)) {
            throw new BalanceTalkException(NOT_FOUND_OPTION);
        }

        Comment comment = createCommentRequest.toEntity(member, talkPick);
        commentRepository.save(comment);
    }

    @Transactional
    public void createCommentReply(CommentDto.CreateCommentRequest createCommentRequest, Long talkPickId, Long commentId) {
        Member member = getCurrentMember(memberRepository);
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
    }

    @Transactional(readOnly = true)
    public Page<CommentDto.CommentResponse> findAllComments(Long talkPickId, String token, Pageable pageable) {
        validateTalkPickId(talkPickId);

        Page<Comment> comments = commentRepository.findAllByTalkPickId(talkPickId, pageable);
        return comments.map(comment -> CommentDto.CommentResponse.fromEntity(comment, false));
    }

    @Transactional(readOnly = true)
    public Page<CommentDto.CommentResponse> findAllBestComments(Long talkPickId, Pageable pageable) {
        validateTalkPickId(talkPickId);

        List<Comment> allComments = commentRepository.findByTalkPickIdOrderByLikesCountDescCreatedAtDesc(talkPickId);
        List<CommentDto.CommentResponse> bestComments = new ArrayList<>();
        List<CommentDto.CommentResponse> otherComments = new ArrayList<>();

        // 최대 좋아요 수 구하기
        int maxLikes = allComments.stream()
                .mapToInt(Comment::getLikesCount)
                .max()
                .orElse(0);

        // 좋아요 10개 이상인 댓글이 있는 경우
        if (maxLikes >= MIN_COUNT_FOR_BEST_COMMENT) {
            for (Comment comment : allComments) {
                boolean myLike = false;
                comment.setIsBest(comment.getLikesCount() >= MIN_COUNT_FOR_BEST_COMMENT);
                CommentDto.CommentResponse response = CommentDto.CommentResponse.fromEntity(comment, myLike);
                if (comment.getIsBest()) {
                    bestComments.add(response);
                } else {
                    otherComments.add(response);
                }
            }
        } else {
            for (Comment comment : allComments) {
                boolean myLike = false;
                comment.setIsBest(comment.getLikesCount() == maxLikes);
                CommentDto.CommentResponse response = CommentDto.CommentResponse.fromEntity(comment, myLike);
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
    
    public void updateComment(Long commentId, Long talkPickId, String content) {
        Comment comment = validateCommentByMemberAndTalkPick(commentId, talkPickId);

        comment.updateContent(content);
    }

    public void deleteComment(Long commentId, Long talkPickId) {
        validateCommentByMemberAndTalkPick(commentId, talkPickId);

        commentRepository.deleteById(commentId);
    }

    private Comment validateCommentByMemberAndTalkPick(Long commentId, Long talkPickId) {
        Member member = getCurrentMember(memberRepository);
        Comment comment = validateCommentId(commentId);
        validateTalkPickId(talkPickId);

        if (!member.equals(comment.getMember())) {
            throw new BalanceTalkException(FORBIDDEN_COMMENT_MODIFY);
        }

        if (!comment.getTalkPick().getId().equals(talkPickId)) {
            throw new BalanceTalkException(NOT_FOUND_COMMENT_AT_THAT_POST);
        }
        return comment;
    }

    private TalkPick validateTalkPickId(Long talkPickId) {
        return talkPickRepository.findById(talkPickId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_TALK_PICK)); // TODO : 에러메시지 수정 필요
    }

    private Comment validateCommentId(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_COMMENT));
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
}
