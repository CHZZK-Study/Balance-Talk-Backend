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
import balancetalk.module.vote.domain.Vote;
import balancetalk.module.vote.domain.VoteRepository;
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

    @Mock
    private VoteRepository voteRepository;

    @Test
    @DisplayName("댓글 생성 성공")
    void createComment_Success() {
        // given
        Long memberId = 1L;
        Long postId = 1L;
        Long selectedOptionId = 1L;
        Long voteId = 1L;
        Vote vote = Vote.builder().id(voteId).build();
        Member member = Member.builder().id(memberId).votes(List.of(vote)).build();
        BalanceOption balanceOption = BalanceOption.builder().id(selectedOptionId).build();
        Post post = Post.builder().id(postId).options(List.of(balanceOption)).build();
        CommentCreateRequest request = new CommentCreateRequest("댓글 내용입니다.", memberId, selectedOptionId);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(voteRepository.findByMemberIdAndBalanceOption_PostId(memberId, postId)).thenReturn(Optional.of(vote));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Comment response = commentService.createComment(request, postId);

        // then
        assertThat(response.getContent()).isEqualTo(request.getContent());
        assertThat(response.getMember()).isEqualTo(member);
        assertThat(response.getPost()).isEqualTo(post);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("게시글에 대한 댓글 조회 성공")
    void readCommentsByPostId_Success() {
        // given
        Long memberId = 1L;
        Long postId = 1L;
        Member mockMember = Member.builder().id(1L).nickname("회원1").build();
        BalanceOption balanceOption = BalanceOption.builder().id(1L).build();
        Post mockPost = Post.builder().id(postId).options(List.of(balanceOption)).build();
        Vote vote = Vote.builder().id(1L).balanceOption(balanceOption).build();

        List<Comment> comments = List.of(
                Comment.builder().id(1L).content("댓글 1").member(mockMember).post(mockPost).build(),
                Comment.builder().id(2L).content("댓글 2").member(mockMember).post(mockPost).build()
        );

        when(commentRepository.findByPostId(postId)).thenReturn(comments);
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
        when(voteRepository.findByMemberIdAndBalanceOption_PostId(memberId, postId)).thenReturn(Optional.of(vote));

        // when
        List<CommentResponse> responses = commentService.findAll(postId);

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

        // when
        Comment updatedComment = commentService.updateComment(commentId, updatedContent);

        // then
        assertThat(updatedComment.getContent()).isEqualTo(updatedContent);
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteComment_Success() {
        // given
        Long commentId = 1L;
        Comment existingComment = Comment.builder().id(commentId).content("기존 댓글 내용").build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        doNothing().when(commentRepository).deleteById(commentId);

        // when
        commentService.deleteComment(commentId);

        // then
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    @DisplayName("댓글 생성 실패 - 회원을 찾을 수 없음")
    void createComment_Fail_MemberNotFound() {
        // given
        Long memberId = 1L;
        Long postId = 1L;
        CommentCreateRequest request = new CommentCreateRequest("댓글 내용입니다.", memberId, null);

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> commentService.createComment(request, postId))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("댓글 생성 실패 - 게시글을 찾을 수 없음")
    void createComment_Fail_PostNotFound() {
        // given
        Long memberId = 1L;
        Long postId = 1L;
        CommentCreateRequest request = new CommentCreateRequest("댓글 내용입니다.", memberId, null);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(Member.builder().id(memberId).build()));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> commentService.createComment(request, postId))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining("존재하지 않는 게시글입니다.");
    }

    @Test
    @DisplayName("댓글 수정 실패 - 댓글을 찾을 수 없음")
    void updateComment_Fail_CommentNotFound() {
        // given
        Long commentId = 1L;
        String updatedContent = "업데이트된 댓글 내용";

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> commentService.updateComment(commentId, updatedContent))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining("존재하지 않는 댓글입니다.");
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 댓글을 찾을 수 없음")
    void deleteComment_Fail_CommentNotFound() {
        // given
        Long commentId = 1L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> commentService.deleteComment(commentId))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining("존재하지 않는 댓글입니다.");
    }

    @Test
    @DisplayName("게시글에 대한 댓글 조회 실패 - 게시글을 찾을 수 없음")
    void readCommentsByPostId_Fail_PostNotFound() {
        // given
        Long postId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> commentService.findAll(postId))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining("존재하지 않는 게시글입니다.");
    }
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
