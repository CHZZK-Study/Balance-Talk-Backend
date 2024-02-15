package balancetalk.module.comment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.module.comment.domain.Comment;
import balancetalk.module.comment.domain.CommentLikeRepository;
import balancetalk.module.comment.domain.CommentRepository;
import balancetalk.module.comment.dto.CommentCreateRequest;
import balancetalk.module.comment.dto.CommentResponse;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Test
    @DisplayName("댓글 생성 성공")
    void createComment_Success() {
        // given
        Long memberId = 1L;
        Long postId = 1L;
        Member member = Member.builder().id(memberId).build();
        Post post = Post.builder().id(postId).options(new ArrayList<>()).build();
        CommentCreateRequest request = new CommentCreateRequest("댓글 내용입니다.", memberId, null);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        CommentResponse response = commentService.createComment(request, postId);

        // then
        assertThat(response.getContent()).isEqualTo(request.getContent());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("게시글에 대한 댓글 조회 성공")
    void readCommentsByPostId_Success() {
        // given
        Long postId = 1L;
        Member mockMember = Member.builder().id(1L).nickname("회원1").build();
        Post mockPost = Post.builder().id(postId).build();

        List<Comment> comments = List.of(
                Comment.builder().id(1L).content("댓글 1").member(mockMember).post(mockPost).build(),
                Comment.builder().id(2L).content("댓글 2").member(mockMember).post(mockPost).build()
        );

        when(commentRepository.findByPostId(postId)).thenReturn(comments);

        // when
        List<CommentResponse> responses = commentService.readCommentsByPostId(postId);

        // then
        assertThat(responses).hasSize(comments.size());
        assertThat(responses.get(0).getContent()).isEqualTo(comments.get(0).getContent());
        assertThat(responses.get(0).getMemberName()).isEqualTo(mockMember.getNickname());
        assertThat(responses.get(0).getPostId()).isEqualTo(postId);
        assertThat(responses.get(1).getContent()).isEqualTo(comments.get(1).getContent());
        assertThat(responses.get(1).getMemberName()).isEqualTo(mockMember.getNickname());
        assertThat(responses.get(1).getPostId()).isEqualTo(postId);
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void updateComment_Success() {
        // given
        Long commentId = 1L;
        String updatedContent = "업데이트된 댓글 내용";
        Comment existingComment = Comment.builder().id(commentId).content("기존 댓글 내용").build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Comment updatedComment = commentService.updateComment(commentId, updatedContent);

        // then
        assertThat(updatedComment.getContent()).isEqualTo(updatedContent);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteComment_Success() {
        // given
        Long commentId = 1L;

        doNothing().when(commentRepository).deleteById(commentId);

        // when
        commentService.deleteComment(commentId);

        // then
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    @DisplayName("사용자가 특정 댓글에 추천을 누르면 해당 댓글 id가 반환된다.")
    void createCommentLike_Success() {
        // given
        Comment comment = Comment.builder()
                .id(1L)
                .build();
        Member member = Member.builder()
                .id(1L)
                .build();

        when(commentRepository.findById(any())).thenReturn(Optional.of(comment));
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));

        // when
        Long likedCommentId = commentService.likeComment(1L, comment.getId(), member.getId());

        // then
        assertThat(likedCommentId).isEqualTo(comment.getId());
    }

    @Test
    @DisplayName("댓글 중복 추천 시 예외 발생")
    void createCommentLike_Fail_ByAlreadyLikeComment() {
        // given
        Comment comment = Comment.builder()
                .id(1L)
                .build();
        Member member = Member.builder()
                .id(1L)
                .build();

        when(commentRepository.findById(any())).thenReturn(Optional.of(comment));
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(commentLikeRepository.existsByMemberAndComment(member, comment))
                .thenThrow(new BalanceTalkException(ErrorCode.ALREADY_LIKE_COMMENT));

        // when, then
        assertThatThrownBy(() -> commentService.likeComment(1L, comment.getId(), member.getId()))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining(ErrorCode.ALREADY_LIKE_COMMENT.getMessage());
    }
}
