package balancetalk.module.bookmark.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.module.bookmark.domain.Bookmark;
import balancetalk.module.bookmark.domain.BookmarkRepository;
import balancetalk.module.bookmark.dto.BookmarkResponse;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static balancetalk.global.exception.ErrorCode.ALREADY_BOOKMARK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


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

    private final String authenticatedEmail = "user@example.com";


    @BeforeEach
    void setUp() {
        // SecurityContext에 인증된 사용자 설정
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn(authenticatedEmail);
    }

    @Test
    @DisplayName("북마크 생성 성공")
    void createBookmark_Success() {
        // given
        Long postId = 1L;
        Member member = Member.builder().email(authenticatedEmail).bookmarks(new ArrayList<>()).build();
        Post post = Post.builder().id(postId).build();
        Bookmark bookmark = Bookmark.builder().member(member).post(post).build();

        when(memberRepository.findByEmail(authenticatedEmail)).thenReturn(Optional.of(member));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(bookmarkRepository.save(any(Bookmark.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Bookmark response = bookmarkService.createBookmark(postId);

        // then
        assertThat(response.getMember()).isEqualTo(member);
        assertThat(response.getPost()).isEqualTo(post);
    }

    @Test
    @DisplayName("북마크 조회 성공")
    void findAllByMember_Success() {
        // given
        Member member = Member.builder().email(authenticatedEmail).bookmarks(new ArrayList<>()).build();

        when(memberRepository.findByEmail(authenticatedEmail)).thenReturn(Optional.of(member));
        when(bookmarkRepository.findByMember(member)).thenReturn(new ArrayList<>());

        // when
        List<BookmarkResponse> result = bookmarkService.findAllByMember();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("북마크 삭제 성공")
    void deleteById_Success() {
        // given
        Long postId = 1L;
        Member member = Member.builder().email(authenticatedEmail).bookmarks(new ArrayList<>()).build();
        Post post = Post.builder().id(postId).build();
        Bookmark bookmark = Bookmark.builder().member(member).post(post).build();

        when(memberRepository.findByEmail(authenticatedEmail)).thenReturn(Optional.of(member));
        when(bookmarkRepository.findByMemberAndPostId(member, postId)).thenReturn(Optional.of(bookmark));

        // when
        bookmarkService.deleteByPostId(postId);

        // then
        verify(bookmarkRepository).delete(bookmark);
    }

    @Test
    @DisplayName("북마크 등록 실패 - 이미 북마크한 게시글")
    void createBookmark_Fail_AlreadyBookmarked() {
        // given
        Long postId = 1L;
        Member member = Member.builder().email(authenticatedEmail).bookmarks(new ArrayList<>()).build();
        Post post = Post.builder().id(postId).build();
        Bookmark bookmark = Bookmark.builder().member(member).post(post).build();

        when(memberRepository.findByEmail(authenticatedEmail)).thenReturn(Optional.of(member));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(bookmarkRepository.save(any(Bookmark.class))).thenThrow(new BalanceTalkException(ALREADY_BOOKMARK));

        // when
        // then
        assertThatThrownBy(() -> bookmarkService.createBookmark(postId))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining("이미 북마크한 게시글입니다.");
    }

    @Test
    @DisplayName("북마크 등록 실패 - 존재하지 않는 게시글")
    void createBookmark_Fail_NotFoundPost() {
        // given
        Long postId = 1L;
        Member member = Member.builder().email(authenticatedEmail).bookmarks(new ArrayList<>()).build();
        Bookmark bookmark = Bookmark.builder().member(member).post(Post.builder().id(postId).build()).build();

        when(memberRepository.findByEmail(authenticatedEmail)).thenReturn(Optional.of(member));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> bookmarkService.createBookmark(postId))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining("존재하지 않는 게시글입니다.");
    }

    @Test
    @DisplayName("북마크 조회 실패 - 존재하지 않는 회원")
    void findAllByMember_Fail_NotFoundMember() {
        // given;
        when(memberRepository.findByEmail(authenticatedEmail)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> bookmarkService.findAllByMember())
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("북마크 삭제 실패 - 존재하지 않는 회원")
    void deleteById_Fail_NotFoundMember() {
        // given
        Long postId = 1L;
        when(memberRepository.findByEmail(authenticatedEmail)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> bookmarkService.deleteByPostId(postId))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("북마크 삭제 실패 - 회원 북마크 리스트에 존재하지 않는 북마크")
    void deleteById_Fail_NotFoundBookmark() {
        // given
        Long postId = 1L;
        Member member = Member.builder().email(authenticatedEmail).bookmarks(new ArrayList<>()).build();

        when(memberRepository.findByEmail(authenticatedEmail)).thenReturn(Optional.of(member));
        when(bookmarkRepository.findByMemberAndPostId(member, postId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> bookmarkService.deleteByPostId(postId))
                .isInstanceOf(BalanceTalkException.class)
                .hasMessageContaining("해당 게시글에서 북마크한 기록이 존재하지 않습니다.");
    }
}