package balancetalk.module.comment.application;

import static balancetalk.global.exception.ErrorCode.*;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.module.comment.domain.Comment;
import balancetalk.module.comment.domain.CommentLike;
import balancetalk.module.comment.domain.CommentLikeRepository;
import balancetalk.module.comment.domain.CommentRepository;
import balancetalk.module.comment.dto.CommentRequest;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static balancetalk.global.exception.ErrorCode.*;
import static balancetalk.global.utils.SecurityUtils.getCurrentMember;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final VoteRepository voteRepository;

    @Value("${comments.max-depth}")
    private int maxDepth;

    public Comment createComment(CommentRequest request, Long postId) {
        Member member = getCurrentMember(memberRepository);
        Post post = validatePostId(postId);
        BalanceOption balanceOption = validateBalanceOptionId(request, post);
        voteRepository.findByMemberIdAndBalanceOption_PostId(member.getId(), postId)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_VOTE));

        Comment comment = request.toEntity(member, post);
        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findAll(Long postId) {
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

        if (!getCurrentMember(memberRepository).equals(comment.getMember())) {
            throw new BalanceTalkException(FORBIDDEN_COMMENT_MODIFY);
        }

        comment.updateContent(content);
        return comment;
    }

    public void deleteComment(Long commentId) {
        validateCommentId(commentId);
        Member commentMember = commentRepository.findById(commentId).get().getMember();

        if (!getCurrentMember(memberRepository).equals(commentMember)) {
            throw new BalanceTalkException(FORBIDDEN_COMMENT_DELETE);
        }

        commentRepository.deleteById(commentId);
    }

    @Transactional
    public Comment createReply(Long postId, Long commentId, ReplyCreateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_POST));

        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_COMMENT));

        Member member = getCurrentMember(memberRepository);

        // 부모 댓글과 연결된 게시글이 맞는지 확인
        if (!parentComment.getPost().equals(post)) {
            throw new BalanceTalkException(NOT_FOUND_PARENT_COMMENT);
        }

        validateDepth(parentComment);

        Comment reply = request.toEntity(member, post, parentComment);
        return commentRepository.save(reply);
    }

    private Post validatePostId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_POST));
    }

    private BalanceOption validateBalanceOptionId(CommentRequest request, Post post) {
        return post.getOptions().stream()
                .filter(option -> option.getId().equals(request.getSelectedOptionId()))
                .findFirst()
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_BALANCE_OPTION));
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

    public Long likeComment(Long postId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_COMMENT));
        Member member = getCurrentMember(memberRepository);

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
        Member member = getCurrentMember(memberRepository);

        commentLikeRepository.deleteByMemberAndComment(member, comment);
    }
}
