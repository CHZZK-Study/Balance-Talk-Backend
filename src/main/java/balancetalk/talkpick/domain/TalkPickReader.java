package balancetalk.talkpick.domain;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.talkpick.domain.repository.TalkPickRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TalkPickReader {

    private final TalkPickRepository talkPickRepository;

    public TalkPick readTalkPickById(Long id) {
        return talkPickRepository.findById(id)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_TALK_PICK));
    }
}
