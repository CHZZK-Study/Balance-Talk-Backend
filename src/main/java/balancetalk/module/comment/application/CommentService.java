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

        Comment comment = Comment.builder()
                .content(request.getContent())
                .member(member)
                .post(post)
                .viewStatus(ViewStatus.NORMAL)
                .build();

        return commentRepository.save(comment);
    }

    public List<CommentResponse> readCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(comment -> new CommentResponse(
                        comment.getId(),
                        comment.getContent(),
                        comment.getMember().getNickname(),
                        comment.getPost().getId(),
                        comment.getLikes().size()))
                .collect(Collectors.toList());
    }

    public Comment updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // 댓글 업데이트를 위한 새로운 메소드 사용
        Comment updatedComment = Comment.builder()
                .id(comment.getId())
                .content(content)
                .member(comment.getMember())
                .post(comment.getPost())
                .viewStatus(comment.getViewStatus())
                .likes(comment.getLikes())
                .reports(comment.getReports())
                .build();

        return commentRepository.save(updatedComment);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
