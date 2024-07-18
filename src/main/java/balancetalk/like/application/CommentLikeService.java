package balancetalk.like.application;

import balancetalk.comment.domain.Comment;
import balancetalk.comment.domain.CommentRepository;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.like.domain.Like;
import balancetalk.like.domain.LikeRepository;
import balancetalk.like.dto.LikeDto;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.talkpick.domain.repository.TalkPickRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import static balancetalk.global.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentRepository commentRepository;

    private final LikeRepository likeRepository;

    private final MemberRepository memberRepository;

    private final TalkPickRepository talkPickRepository;

    @Transactional
    public void likeComment(Long commentId, Long talkPickId, ApiMember apiMember) {
        // 톡픽, 댓글, 회원 존재 여부 예외 처리
        validateTalkPick(talkPickId);
        Member member = apiMember.toMember(memberRepository);

        // 톡픽에 속한 댓글이 아닐 경우 예외 처리
        Comment comment = validateCommentByTalkPick(commentId, talkPickId);

        // 본인 댓글에는 좋아요 불가
        if (comment.getMember().getId().equals(member.getId())) {
            throw new BalanceTalkException(FORBIDDEN_LIKE_OWN_COMMENT);
        }

        // 이미 좋아요를 누른 댓글일 경우 예외 처리
        Optional<Like> existingLike = likeRepository.findByResourceIdAndMemberId(commentId, member.getId());

        if (existingLike.isPresent() && existingLike.get().getActive()) {
            throw new BalanceTalkException(ALREADY_LIKED_COMMENT);
        }

        // 이미 좋아요가 존재하지만 비활성화 상태인 경우 활성화 처리
        if (existingLike.isPresent()) {
            Like commentLike = existingLike.get();
            commentLike.activate();
        } else {
            Like commentLike = LikeDto.CreateLikeRequest.toEntity(commentId, member);
            likeRepository.save(commentLike);
        }
    }

    @Transactional
    public void unLikeComment(Long commentId, Long talkPickId, ApiMember apiMember) {
        validateTalkPick(talkPickId);
        Member member = apiMember.toMember(memberRepository);

        // 톡픽에 속한 댓글이 아닐 경우 예외 처리
        Comment comment = validateCommentByTalkPick(commentId, talkPickId);

        // 좋아요를 누르지 않은 댓글에 좋아요 취소를 누를 경우 예외 처리
        Like commentLike = likeRepository.findByResourceIdAndMemberId(comment.getId(), member.getId())
                .orElseThrow(() -> new BalanceTalkException(NOT_LIKED_COMMENT));

        if (!commentLike.getActive()) {
            throw new BalanceTalkException(NOT_LIKED_COMMENT);
        }

        commentLike.deActive();
    }

    private void validateTalkPick(Long talkPickId) {
        talkPickRepository.findById(talkPickId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_TALK_PICK));
    }

    private Comment validateCommentByTalkPick(Long commentId, Long talkPickId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_COMMENT));

        if (!comment.getTalkPick().getId().equals(talkPickId)) {
            throw new BalanceTalkException(NOT_FOUND_COMMENT_AT_THAT_TALK_PICK);
        }

        return comment;
    }
}
