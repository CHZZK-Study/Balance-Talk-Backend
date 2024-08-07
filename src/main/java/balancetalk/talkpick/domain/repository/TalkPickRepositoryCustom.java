package balancetalk.talkpick.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static balancetalk.talkpick.dto.TalkPickDto.TalkPickResponse;
import static balancetalk.talkpick.dto.TodayTalkPickDto.TodayTalkPickResponse;

public interface TalkPickRepositoryCustom {

    TodayTalkPickResponse findTodayTalkPick();

    Page<TalkPickResponse> findPagedTalkPicks(Pageable pageable);

    List<TalkPickResponse> findBestTalkPicks();
}
