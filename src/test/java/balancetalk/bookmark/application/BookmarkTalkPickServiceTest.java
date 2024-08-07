package balancetalk.bookmark.application;

import balancetalk.bookmark.domain.Bookmark;
import balancetalk.bookmark.domain.BookmarkGenerator;
import balancetalk.bookmark.domain.BookmarkRepository;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.TalkPickReader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static balancetalk.bookmark.domain.BookmarkType.TALK_PICK;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookmarkTalkPickServiceTest {

    @InjectMocks
    BookmarkTalkPickService bookmarkTalkPickService;

    @Mock
    TalkPickReader talkPickReader;

    @Mock
    MemberRepository memberRepository;

    @Mock
    BookmarkGenerator bookmarkGenerator;

    @Mock
    BookmarkRepository bookmarkRepository;

    ApiMember apiMember;

    @BeforeEach
    void setUp() {
        apiMember = new ApiMember(1L);
    }

    @Test
    @DisplayName("북마크를 성공적으로 생성합니다.")
    void createBookmark_Success_ThenSaveNewBookmarkEntity() {
        // given
        Member member = mock(Member.class);
        Bookmark bookmark = mock(Bookmark.class);
        TalkPick talkPick = mock(TalkPick.class);

        when(talkPickReader.readById(any())).thenReturn(talkPick);
        when(memberRepository.findById(any())).thenReturn(Optional.ofNullable(member));
        when(bookmarkGenerator.generate(1L, TALK_PICK, member)).thenReturn(bookmark);

        // when
        bookmarkTalkPickService.createBookmark(1L, apiMember);

        // then
        verify(bookmarkRepository).save(bookmark);
    }

    @Test
    void createBookmark_Success_ThenActivateBookmark() {
        // given
        Bookmark bookmark = Bookmark.builder()
                .resourceId(1L)
                .bookmarkType(TALK_PICK)
                .active(false)
                .build();

        Member member = Member.builder()
                .talkPicks(List.of())
                .bookmarks(List.of(bookmark))
                .build();

        TalkPick talkPick = mock(TalkPick.class);

        when(talkPickReader.readById(any())).thenReturn(talkPick);
        when(memberRepository.findById(any())).thenReturn(Optional.ofNullable(member));

        // when
        bookmarkTalkPickService.createBookmark(1L, apiMember);

        // then
        Assertions.assertThat(bookmark.getActive()).isTrue();
    }

    @Test
    void deleteBookmark_Success_ThenDeactivateBookmark() {
        // given
        Bookmark bookmark = Bookmark.builder()
                .resourceId(1L)
                .bookmarkType(TALK_PICK)
                .active(true)
                .build();

        Member member = Member.builder()
                .talkPicks(List.of())
                .bookmarks(List.of(bookmark))
                .build();

        TalkPick talkPick = mock(TalkPick.class);

        when(talkPickReader.readById(any())).thenReturn(talkPick);
        when(memberRepository.findById(any())).thenReturn(Optional.ofNullable(member));

        // when
        bookmarkTalkPickService.deleteBookmark(1L, apiMember);

        // then
        Assertions.assertThat(bookmark.getActive()).isFalse();
    }
}