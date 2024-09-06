package balancetalk.talkpick.application;

import balancetalk.talkpick.domain.repository.SearchTalkPickRepository;
import balancetalk.talkpick.dto.SearchTalkPickResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchTalkPickService {

    private final SearchTalkPickRepository searchTalkPickRepository;

    public Page<SearchTalkPickResponse> searchTalkPicks(final String query, Pageable pageable) {
        return searchTalkPickRepository.searchTalkPicks(query, pageable);
    }
}
