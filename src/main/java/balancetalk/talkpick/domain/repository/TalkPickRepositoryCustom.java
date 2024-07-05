package balancetalk.talkpick.domain.repository;

import static balancetalk.talkpick.dto.TodayTalkPickDto.TodayTalkPickResponse;

public interface TalkPickRepositoryCustom {

    TodayTalkPickResponse findTodayTalkPick();
}
