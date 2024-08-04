package balancetalk.talkpick.application;

import balancetalk.member.domain.Member;
import balancetalk.member.dto.GuestOrApiMember;
import balancetalk.talkpick.domain.Summary;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.TalkPickReader;
import balancetalk.talkpick.dto.TalkPickDto.TalkPickDetailResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static balancetalk.vote.domain.VoteOption.A;
import static balancetalk.vote.domain.VoteOption.B;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TalkPickServiceTest {

    @InjectMocks
    TalkPickService talkPickService;

    @Mock
    TalkPickReader talkPickReader;

    TalkPick talkPick;
    GuestOrApiMember guestOrApiMember;
    Member member;

    @BeforeEach
    void setUp() {
        talkPick = mock(TalkPick.class);
        member = mock(Member.class);
        guestOrApiMember = mock(GuestOrApiMember.class);

        when(talkPick.getId()).thenReturn(1L);
        when(talkPick.getTitle()).thenReturn("Sample Title");
        when(talkPick.getContent()).thenReturn("Sample Content");
        when(talkPick.getSummary()).thenReturn(new Summary());
        when(talkPick.getOptionA()).thenReturn("Option A");
        when(talkPick.getOptionB()).thenReturn("Option B");
        when(talkPick.votesCountOf(A)).thenReturn(12L);
        when(talkPick.votesCountOf(B)).thenReturn(12L);
        when(talkPick.getViews()).thenReturn(152L);
        when(talkPick.getWriterNickname()).thenReturn("writer");
        when(talkPick.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(talkPick.getLastModifiedAt()).thenReturn(LocalDateTime.now());
    }

    @Test
    @DisplayName("톡픽을 조회하면 해당 톡픽의 조회수가 1 증가한다.")
    void findById_Success_ThenIncreaseViews() {
        // given
        when(talkPickReader.readById(1L)).thenReturn(talkPick);
        when(guestOrApiMember.isGuest()).thenReturn(true);

        // when
        talkPickService.findById(1L, guestOrApiMember);

        // then
        verify(talkPick).increaseViews();
    }

    @Test
    @DisplayName("비회원이 톡픽을 조회하면 그 응답의 북마크 여부는 false가 된다.")
    void findById_Success_ThenMyBookmarkIsFalse_ByGuest() {
        // given
        when(talkPickReader.readById(1L)).thenReturn(talkPick);
        when(guestOrApiMember.isGuest()).thenReturn(true);

        // when
        TalkPickDetailResponse result = talkPickService.findById(1L, guestOrApiMember);

        // then
        assertThat(result.getMyBookmark()).isFalse();
    }

    @Test
    @DisplayName("비회원이 톡픽을 조회하면 그 응답의 투표 선택지는 null이 된다.")
    void findById_Success_ThenVoteOptionIsNull_ByGuest() {
        // given
        when(talkPickReader.readById(1L)).thenReturn(talkPick);
        when(guestOrApiMember.isGuest()).thenReturn(true);

        // when
        TalkPickDetailResponse result = talkPickService.findById(1L, guestOrApiMember);

        // then
        assertThat(result.getVotedOption()).isNull();
    }
}