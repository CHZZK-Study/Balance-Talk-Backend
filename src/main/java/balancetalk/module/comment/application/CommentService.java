package balancetalk.module.comment.application;

import static balancetalk.global.exception.ErrorCode.NOT_FOUND_BALANCE_OPTION;
import static balancetalk.global.exception.ErrorCode.NOT_FOUND_COMMENT;
import static balancetalk.global.exception.ErrorCode.NOT_FOUND_MEMBER;
import static balancetalk.global.exception.ErrorCode.NOT_FOUND_POST;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.module.comment.domain.Comment;
import balancetalk.module.comment.domain.CommentRepository;
import balancetalk.module.comment.dto.CommentCreateRequest;
import balancetalk.module.comment.dto.CommentResponse;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.post.domain.BalanceOption;
import balancetalk.module.post.domain.BalanceOptionRepository;
import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public CommentResponse createComment(CommentCreateRequest request, Long postId) {
        Member member = validateMemberId(request);
        Post post = validatePostId(postId);
        validateBalanceOptionId(request, post);

        Comment comment = request.toEntity(member, post);
        comment = commentRepository.save(comment);
        return CommentResponse.fromEntity(comment);
    }

    @Transactional
    public List<CommentResponse> readCommentsByPostId(Long postId) {
        validatePostId(postId);

        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(CommentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public Comment updateComment(Long commentId, String content) {
        Comment comment = validateCommentId(commentId);

        comment.updateContent(content);
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        validateCommentId(commentId);
        commentRepository.deleteById(commentId);
    }

    private Member validateMemberId(CommentCreateRequest request) {
        return memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_MEMBER));
    }

    private Post validatePostId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_POST));
    }

    private BalanceOption validateBalanceOptionId(CommentCreateRequest request, Post post) {
        return post.getOptions().stream()
                .filter(option -> option.getId().equals(request.getBalanceOptionId()))
                .findFirst()
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_BALANCE_OPTION));
    }

    private Comment validateCommentId(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_COMMENT));
    }
}
