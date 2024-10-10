package balancetalk.bookmark.application;

import balancetalk.bookmark.domain.GameBookmarkRepository;
import balancetalk.bookmark.domain.TalkPickBookmark;
import balancetalk.bookmark.domain.BookmarkGenerator;
import balancetalk.bookmark.domain.TalkPickBookmarkRepository;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TalkPickBookmarkServiceTest {

    @InjectMocks
    BookmarkTalkPickService bookmarkTalkPickService;

    @Mock
    TalkPickReader talkPickReader;

    @Mock
    MemberRepository memberRepository;

    @Mock
    BookmarkGenerator bookmarkGenerator;

    @Mock
    TalkPickBookmarkRepository talkPickBookmarkRepository;

    @Mock
    GameBookmarkRepository gameBookmarkRepository;

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
        TalkPickBookmark talkPickBookmark = mock(TalkPickBookmark.class);
        TalkPick talkPick = mock(TalkPick.class);

        when(talkPickReader.readById(any())).thenReturn(talkPick);
        when(memberRepository.findById(any())).thenReturn(Optional.ofNullable(member));
        when(bookmarkGenerator.generate(talkPick, member)).thenReturn(talkPickBookmark);

        // when
        bookmarkTalkPickService.createBookmark(1L, apiMember);

        // then
        verify(talkPickBookmarkRepository).save(talkPickBookmark);
    }

    @Test
    void createBookmark_Success_ThenActivateBookmark() {
        // given
        TalkPick talkPick = mock(TalkPick.class);

        TalkPickBookmark talkPickBookmark = TalkPickBookmark.builder()
                .talkPick(talkPick)
                .active(false)
                .build();

        Member member = Member.builder()
                .talkPicks(List.of())
                .talkPickBookmarks(List.of(talkPickBookmark))
                .build();


        when(talkPickReader.readById(any())).thenReturn(talkPick);
        when(memberRepository.findById(any())).thenReturn(Optional.ofNullable(member));

        // when
        bookmarkTalkPickService.createBookmark(1L, apiMember);

        // then
        Assertions.assertThat(talkPickBookmark.getActive()).isTrue();
    }

    @Test
    void deleteBookmark_Success_ThenDeactivateBookmark() {
        // given
        TalkPick talkPick = mock(TalkPick.class);

        TalkPickBookmark talkPickBookmark = TalkPickBookmark.builder()
                .talkPick(talkPick)
                .active(true)
                .build();

        Member member = Member.builder()
                .talkPicks(List.of())
                .talkPickBookmarks(List.of(talkPickBookmark))
                .build();


        when(talkPickReader.readById(any())).thenReturn(talkPick);
        when(memberRepository.findById(any())).thenReturn(Optional.ofNullable(member));

        // when
        bookmarkTalkPickService.deleteBookmark(1L, apiMember);

        // then
        Assertions.assertThat(talkPickBookmark.getActive()).isFalse();
    }
}