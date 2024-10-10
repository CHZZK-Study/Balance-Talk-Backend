package balancetalk.bookmark.domain;

import balancetalk.member.domain.Member;
import balancetalk.talkpick.domain.TalkPick;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class BookmarkTalkPickGeneratorTest {

    BookmarkGenerator bookmarkGenerator;
    Member member;

    @BeforeEach
    void setUp() {
        bookmarkGenerator = new BookmarkGenerator();
        member = Member.builder()
                .id(1L)
                .build();
    }

    @Test
    @DisplayName("북마크 객체를 성공적으로 생성합니다.")
    void generate_Success_ThenReturnBookmark() {
        // given
        TalkPick talkPick = mock(TalkPick.class);

        // when
        TalkPickBookmark bookmark = bookmarkGenerator.generate(talkPick, member);

        // then
        assertThat(bookmark.getTalkPick()).isEqualTo(talkPick);
        assertThat(bookmark.getMember()).isEqualTo(member);
    }

    @Test
    void generate_Success_ThenActiveIsTrue() {
        // given
        TalkPick talkPick = mock(TalkPick.class);

        // when
        TalkPickBookmark bookmark = bookmarkGenerator.generate(talkPick, member);

        // then
        assertThat(bookmark.getActive()).isTrue();
    }
}