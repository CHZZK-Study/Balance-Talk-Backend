package balancetalk.bookmark.domain;

import balancetalk.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static balancetalk.bookmark.domain.BookmarkType.TALK_PICK;
import static org.assertj.core.api.Assertions.assertThat;

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
        // when
        TalkPickBookmark bookmark = bookmarkGenerator.generate(1L, TALK_PICK, member);

        // then
        assertThat(bookmark.getResourceId()).isEqualTo(1L);
        assertThat(bookmark.getBookmarkType()).isEqualTo(TALK_PICK);
        assertThat(bookmark.getMember()).isEqualTo(member);
    }

    @Test
    void generate_Success_ThenActiveIsTrue() {
        // when
        TalkPickBookmark bookmark = bookmarkGenerator.generate(1L, TALK_PICK, member);

        // then
        assertThat(bookmark.getActive()).isTrue();
    }
}