package balancetalk.talkpick.domain;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.talkpick.domain.repository.TalkPickRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TalkPickReaderTest {

    @InjectMocks
    TalkPickReader talkPickReader;

    @Mock
    TalkPickRepository talkPickRepository;

    @Test
    @DisplayName("ID에 해당하는 톡픽을 가져온다.")
    void readTalkPickById_Success() {
        // given
        TalkPick talkPick = new TalkPick();
        when(talkPickRepository.findById(any())).thenReturn(Optional.of(talkPick));

        // when
        TalkPick result = talkPickReader.readById(1L);

        // then
        assertThat(result).isEqualTo(talkPick);
    }

    @Test
    @DisplayName("존재하지 않는 톡픽의 ID를 통해 톡픽을 조회하려 하면 실패한다.")
    void readTalkPickById_Fail_ByNotFoundTalkPick() {
        // when, then
        assertThatThrownBy(() -> talkPickReader.readById(1L))
                .isInstanceOf(BalanceTalkException.class);
    }
}