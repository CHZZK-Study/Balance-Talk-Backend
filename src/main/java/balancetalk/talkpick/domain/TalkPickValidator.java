package balancetalk.talkpick.domain;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.talkpick.domain.repository.TalkPickRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TalkPickValidator {

    private final TalkPickRepository talkPickRepository;

    public void validateExistence(final long id) {
        if (!talkPickRepository.existsById(id)) {
            throw new BalanceTalkException(ErrorCode.NOT_FOUND_TALK_PICK);
        }
    }
}
