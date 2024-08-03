package balancetalk.talkpick.domain.repository;

import balancetalk.talkpick.dto.TalkPickDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static balancetalk.talkpick.dto.TalkPickDto.*;
import static balancetalk.talkpick.dto.TodayTalkPickDto.TodayTalkPickResponse;

public interface TalkPickRepositoryCustom {

    TodayTalkPickResponse findTodayTalkPick();

    Page<TalkPickResponse> findPagedTalkPicks(Pageable pageable);
}
