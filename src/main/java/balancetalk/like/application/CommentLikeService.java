package balancetalk.like.application;

import balancetalk.comment.domain.Comment;
import balancetalk.comment.domain.CommentRepository;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.like.domain.Like;
import balancetalk.like.domain.LikeRepository;
import balancetalk.like.dto.LikeDto;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static balancetalk.global.utils.SecurityUtils.getCurrentMember;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentRepository commentRepository;

    private final LikeRepository likeRepository;

    private final MemberRepository memberRepository;

    public LikeDto.LikeResponse likeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_COMMENT));

        Member member = getCurrentMember(memberRepository);
        /* TODO : 추후 예외처리
        boolean alreadyLiked = likeRepository.existsByCommentIdAndMemberId(commentId, member.getId());
        if (alreadyLiked) {
            throw new BalanceTalkException(ErrorCode.ALREADY_LIKED);
        }

         */

        Like commentLike = LikeDto.CreateLikeRequest.toEntity(comment, member);
        likeRepository.save(commentLike);

        return LikeDto.LikeResponse.fromEntity(commentLike);
    }

    public void unLikeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_COMMENT)); // TODO : 추후 메서드 분리

        Member member = getCurrentMember(memberRepository);

        Like commentLike = likeRepository.findByCommentIdAndMemberId(commentId, member.getId());

        commentLike.deActive();

    }

    // TODO : 추후 예외처리
}
