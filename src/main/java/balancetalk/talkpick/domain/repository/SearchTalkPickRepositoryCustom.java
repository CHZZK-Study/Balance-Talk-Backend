package balancetalk.talkpick.domain.repository;

import balancetalk.talkpick.dto.SearchTalkPickResponse;

import java.util.List;

public interface SearchTalkPickRepositoryCustom {

    List<SearchTalkPickResponse> searchTalkPicks(String query);
}
