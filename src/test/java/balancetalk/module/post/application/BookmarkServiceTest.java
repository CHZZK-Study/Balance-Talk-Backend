package balancetalk.module.post.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.post.domain.Bookmark;
import balancetalk.module.post.domain.BookmarkRepository;
import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostRepository;
import balancetalk.module.post.dto.BookmarkRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static balancetalk.global.exception.ErrorCode.ALREADY_BOOKMARK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {
    @InjectMocks
    private BookmarkService bookmarkService;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PostRepository postRepository;

    @Test
    @DisplayName("북마크 생성 성공")
    void createBookmark_Success() {
        // given
        Long memberId = 1L;
        Long postId = 1L;
        Member member = Member.builder().id(memberId).bookmarks(new ArrayList<>()).build();
        Post post = Post.builder().id(postId).build();
        BookmarkRequestDto request = new BookmarkRequestDto(memberId, postId);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(bookmarkRepository.save(any(Bookmark.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Bookmark response = bookmarkService.save(request, postId);

        // then
        assertThat(response.getMember()).isEqualTo(member);
        assertThat(response.getPost()).isEqualTo(post);
    }

    @Test
    @DisplayName("북마크 조회 성공")
    void findAllByMember_Success() {
        // given
        Long memberId = 1L;
        Member member = Member.builder().id(memberId).bookmarks(new ArrayList<>()).build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(bookmarkRepository.findByMember(member)).thenReturn(new ArrayList<>());

        // when
        bookmarkService.findAllByMember(memberId);

        // then
        assertThat(bookmarkRepository.findByMember(member)).isEmpty();
    }

    @Test
    @DisplayName("북마크 삭제 성공")
    void deleteById_Success() {
        // given
        Long memberId = 1L;
        Long bookmarkId = 1L;
        Member member = Member.builder().id(memberId).bookmarks(new ArrayList<>()).build();
        Bookmark bookmark = Bookmark.builder().id(bookmarkId).build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(bookmarkRepository.findById(bookmarkId)).thenReturn(Optional.of(bookmark));

        // when
        bookmarkService.deleteById(memberId, bookmarkId);

        // then
        verify(bookmarkRepository).deleteById(bookmarkId);
    }

    @Test
    @DisplayName("북마크 등록 실패 - 이미 북마크한 게시글")
    void createBookmark_Fail_AlreadyBookmarked() {
        // given
        Long memberId = 1L;
        Long postId = 1L;
        Member member = Member.builder().id(memberId).bookmarks(new ArrayList<>()).build();
        Post post = Post.builder().id(postId).build();
        BookmarkRequestDto request = new BookmarkRequestDto(memberId, postId);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(bookmarkRepository.save(any(Bookmark.class))).thenThrow(new BalanceTalkException(ALREADY_BOOKMARK));

        // when
        // then
        assertThatThrownBy(() -> bookmarkService.save(request, postId))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining("이미 북마크한 게시글입니다.");
    }

    @Test
    @DisplayName("북마크 등록 실패 - 존재하지 않는 게시글")
    void createBookmark_Fail_NotFoundPost() {
        // given
        Long memberId = 1L;
        Long postId = 1L;
        Member member = Member.builder().id(memberId).bookmarks(new ArrayList<>()).build();
        BookmarkRequestDto request = new BookmarkRequestDto(memberId, postId);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> bookmarkService.save(request, postId))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining("존재하지 않는 게시글입니다.");
    }

    @Test
    @DisplayName("북마크 조회 실패 - 존재하지 않는 회원")
    void findAllByMember_Fail_NotFoundMember() {
        // given
        Long memberId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> bookmarkService.findAllByMember(memberId))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("북마크 삭제 실패 - 존재하지 않는 회원")
    void deleteById_Fail_NotFoundMember() {
        // given
        Long memberId = 1L;
        Long bookmarkId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> bookmarkService.deleteById(memberId, bookmarkId))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("북마크 삭제 실패 - 회원 북마크 리스트에 존재하지 않는 북마크")
    void deleteById_Fail_NotFoundBookmark() {
        // given
        Long memberId = 1L;
        Long bookmarkId = 1L;
        Member member = Member.builder().id(memberId).bookmarks(new ArrayList<>()).build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        // then
        assertThatThrownBy(() -> bookmarkService.deleteById(memberId, bookmarkId))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining("해당 게시글에서 북마크한 기록이 존재하지 않습니다.");
    }
}