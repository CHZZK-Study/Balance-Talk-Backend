package balancetalk.talkpick.application;

import balancetalk.talkpick.domain.repository.TalkPickRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static balancetalk.talkpick.dto.TodayTalkPickDto.TodayTalkPickResponse;

@Service
@RequiredArgsConstructor
public class TodayTalkPickService {

    private final TalkPickRepository talkPickRepository;

    public TodayTalkPickResponse findTodayTalkPick() {
        return talkPickRepository.findTodayTalkPick();
    }
}
