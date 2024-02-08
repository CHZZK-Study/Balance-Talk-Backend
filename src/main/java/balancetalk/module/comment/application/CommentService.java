package balancetalk.module.comment.application;

import balancetalk.module.comment.domain.Comment;
import balancetalk.module.comment.domain.CommentRepository;
import balancetalk.module.comment.dto.CommentCreateRequest;
import balancetalk.module.comment.dto.CommentResponse;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.post.domain.Post;
import balancetalk.module.ViewStatus;
import balancetalk.module.post.domain.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public Comment createComment(CommentCreateRequest request) {
        Member member = memberRepository.findById(request.getMemberId()).orElseThrow(() -> new RuntimeException("Member not found"));
        Post post = postRepository.findById(request.getPostId()).orElseThrow(() -> new RuntimeException("Post not found"));
        Comment comment = request.toEntity(member, post);

        return commentRepository.save(comment);
    }

    public List<CommentResponse> readCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(CommentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public Comment updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.updateContent(content);

        return comment;
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
