package balancetalk.talkpick.application;

import balancetalk.talkpick.domain.repository.SearchTalkPickRepository;
import balancetalk.talkpick.dto.SearchTalkPickResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchTalkPickService {

    private final SearchTalkPickRepository searchTalkPickRepository;

    public List<SearchTalkPickResponse> searchLimitedTalkPicks(final String query) {
        return searchTalkPickRepository.searchLimitedTalkPicks(query);
    }
}
