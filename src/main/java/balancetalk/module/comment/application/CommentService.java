package balancetalk.module.comment.application;

import balancetalk.module.comment.domain.Comment;
import balancetalk.module.comment.domain.CommentRepository;
import balancetalk.module.comment.dto.CommentCreateRequest;
import balancetalk.module.comment.dto.CommentResponse;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
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
    private final BalanceOptionRepository balanceOptionRepository;

    @Transactional
    public CommentResponse createComment(CommentCreateRequest request, Long postId) {
        Member member = memberRepository.findById(request.getMemberId()).orElseThrow(() -> new RuntimeException("Member not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        /* BalanceOption balanceOption = post.getOptions().stream()
                .filter(option -> option.getId().equals(request.getBalanceOptionId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("BalanceOption not found"));
                //TODO : 추후 밸런스 옵션 구현 시 사용할 기능
         */

        Comment comment = request.toEntity(member, post);
        comment = commentRepository.save(comment);

        return CommentResponse.fromEntity(comment);
    }

    @Transactional
    public List<CommentResponse> readCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(CommentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public Comment updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.updateContent(content);

        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
