package balancetalk.module.comment.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.module.comment.domain.Comment;
import balancetalk.module.comment.domain.CommentLike;
import balancetalk.module.comment.domain.CommentLikeRepository;
import balancetalk.module.comment.domain.CommentRepository;
import balancetalk.module.comment.dto.CommentCreateRequest;
import balancetalk.module.comment.dto.CommentResponse;
import balancetalk.module.comment.dto.ReplyCreateRequest;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.post.domain.BalanceOption;
import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostRepository;
import balancetalk.module.vote.domain.Vote;
import balancetalk.module.vote.domain.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static balancetalk.global.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final VoteRepository voteRepository;

    public Comment createComment(CommentCreateRequest request, Long postId) {
        Member member = getCurrentMember();
        Post post = validatePostId(postId);
        BalanceOption balanceOption = validateBalanceOptionId(request, post);
        voteRepository.findByMemberIdAndBalanceOption_PostId(request.getMemberId(), postId)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_VOTE));

        Comment comment = request.toEntity(member, post);
        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findAll(Long postId) { // TODO: 탈퇴한 회원의 정보는 어떻게 표시되는가?
        validatePostId(postId);

        List<Comment> comments = commentRepository.findByPostId(postId);
        List<CommentResponse> responses = new ArrayList<>();

        for (Comment comment : comments) {
            Optional<Vote> voteForComment = voteRepository.findByMemberIdAndBalanceOption_PostId(comment.getMember().getId(), postId);

            Long balanceOptionId = voteForComment.map(Vote::getBalanceOption).map(BalanceOption::getId)
                    .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_BALANCE_OPTION));
            CommentResponse response = CommentResponse.fromEntity(comment, balanceOptionId);
            responses.add(response);
        }
        return responses;
    }

    public Comment updateComment(Long commentId, String content) {
        Comment comment = validateCommentId(commentId);

        comment.updateContent(content);
        return comment;
    }

    public void deleteComment(Long commentId) {
        validateCommentId(commentId);
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public Comment createReply(Long postId, Long commentId, ReplyCreateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_POST));

        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_COMMENT));

        Member member = getCurrentMember();


        // 부모 댓글과 연결된 게시글이 맞는지 확인
        if (!parentComment.getPost().equals(post)) {
            throw new BalanceTalkException(NOT_FOUND_PARENT_COMMENT);
        }

        Comment reply = request.toEntity(member, post, parentComment);
        return commentRepository.save(reply);
    }

    private Post validatePostId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_POST));
    }

    private BalanceOption validateBalanceOptionId(CommentCreateRequest request, Post post) {
        return post.getOptions().stream()
                .filter(option -> option.getId().equals(request.getSelectedOptionId()))
                .findFirst()
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_BALANCE_OPTION));
    }

    private Comment validateCommentId(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_COMMENT));
    }
      
    public Long likeComment(Long postId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_COMMENT));
        Member member = getCurrentMember();

        if (commentLikeRepository.existsByMemberAndComment(member, comment)) {
            throw new BalanceTalkException(ErrorCode.ALREADY_LIKE_COMMENT);
        }

        CommentLike commentLike = CommentLike.builder()
                .comment(comment)
                .member(member)
                .build();
        commentLikeRepository.save(commentLike);

        return comment.getId();
    }

    public void cancelLikeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_COMMENT));
        Member member = getCurrentMember();

        commentLikeRepository.deleteByMemberAndComment(member, comment);
    }

    private Member getCurrentMember() { // TODO: global static 메서드로 전환?
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_MEMBER));
    }
}
