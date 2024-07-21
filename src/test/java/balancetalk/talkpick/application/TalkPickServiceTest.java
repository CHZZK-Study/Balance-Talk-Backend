package balancetalk.talkpick.application;

import balancetalk.member.dto.GuestOrApiMember;
import balancetalk.talkpick.domain.Summary;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.TalkPickReader;
import balancetalk.talkpick.dto.TalkPickDto.TalkPickDetailResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TalkPickServiceTest {

    @InjectMocks
    TalkPickService talkPickService;

    @Mock
    TalkPickReader talkPickReader;

    @Mock
    GuestOrApiMember guestOrApiMember;

    @Test
    @DisplayName("톡픽을 조회하면 해당 톡픽의 조회수가 1 증가한다.")
    void findById_Success_ThenIncreaseViews() {
        // given
        TalkPick talkPick = TalkPick.builder()
                .id(1L)
                .summary(new Summary())
                .votes(List.of())
                .views(0L)
                .build();

        when(talkPickReader.readTalkPickById(1L)).thenReturn(talkPick);
        when(guestOrApiMember.isGuest()).thenReturn(true);

        // when
        TalkPickDetailResponse result = talkPickService.findById(1L, guestOrApiMember);

        // then
        assertThat(result.getViews()).isEqualTo(1);
    }

    @Test
    @DisplayName("비회원이 톡픽을 조회하면 그 응답의 북마크 여부는 false가 된다.")
    void findById_Success_ThenMyBookmarkIsFalse_ByGuest() {
        // given
        TalkPick talkPick = TalkPick.builder()
                .id(1L)
                .summary(new Summary())
                .votes(List.of())
                .views(0L)
                .build();

        when(talkPickReader.readTalkPickById(1L)).thenReturn(talkPick);
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
        TalkPick talkPick = TalkPick.builder()
                .id(1L)
                .summary(new Summary())
                .votes(List.of())
                .views(0L)
                .build();

        when(talkPickReader.readTalkPickById(1L)).thenReturn(talkPick);
        when(guestOrApiMember.isGuest()).thenReturn(true);

        // when
        TalkPickDetailResponse result = talkPickService.findById(1L, guestOrApiMember);

        // then
        assertThat(result.getVotedOption()).isNull();
    }
}