package balancetalk.like.application;

import balancetalk.comment.domain.Comment;
import balancetalk.comment.domain.CommentRepository;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.like.domain.Like;
import balancetalk.like.domain.LikeRepository;
import balancetalk.like.dto.LikeDto;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static balancetalk.global.exception.ErrorCode.*;
import static balancetalk.global.utils.SecurityUtils.getCurrentMember;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentRepository commentRepository;

    private final LikeRepository likeRepository;

    private final MemberRepository memberRepository;

    public LikeDto.LikeResponse likeComment(Long commentId) {
        Comment comment = validateComment(commentId);

        Member member = getCurrentMember(memberRepository);

        // 이미 좋아요를 누른 댓글일 경우 예외 처리
        boolean alreadyLiked = likeRepository.existsByCommentIdAndMemberId(commentId, member.getId());

        if (alreadyLiked) {
            throw new BalanceTalkException(ALREADY_LIKED_COMMENT);
        }

        Like commentLike = LikeDto.CreateLikeRequest.toEntity(comment, member);
        likeRepository.save(commentLike);

        return LikeDto.LikeResponse.fromEntity(commentLike); // TODO : 제거
    }

    public void unLikeComment(Long commentId) {
        validateComment(commentId);

        Member member = getCurrentMember(memberRepository);

        // 좋아요를 누르지 않은 댓글에 좋아요 취소를 누를 경우 예외 처리
        Like commentLike = likeRepository.findByCommentIdAndMemberId(commentId, member.getId())
                .orElseThrow(() -> new BalanceTalkException(NOT_LIKED_COMMENT));

        commentLike.deActive();
    }

    private Comment validateComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_COMMENT));
    }
}
