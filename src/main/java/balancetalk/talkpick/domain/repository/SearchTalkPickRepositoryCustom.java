package balancetalk.talkpick.domain.repository;

import balancetalk.talkpick.dto.SearchTalkPickResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchTalkPickRepositoryCustom {

    Page<SearchTalkPickResponse> searchTalkPicks(String query, Pageable pageable);
}
