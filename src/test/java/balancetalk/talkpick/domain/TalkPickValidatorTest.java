package balancetalk.talkpick.domain;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.talkpick.domain.repository.TalkPickRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TalkPickValidatorTest {

    @InjectMocks
    TalkPickValidator talkPickValidator;

    @Mock
    TalkPickRepository talkPickRepository;

    @Test
    @DisplayName("톡픽 ID를 통해 톡픽 조회 시 존재하지 않을 경우 예외를 발생시킵니다.")
    void validateExistence_ThrowException_ByNotFoundTalkPick() {
        // given
        when(talkPickRepository.existsById(any())).thenReturn(false);

        // when, then
        assertThatThrownBy(() -> talkPickValidator.validateExistence(1L))
                .isInstanceOf(BalanceTalkException.class);
    }
}