package balancetalk.game.application;

import static balancetalk.global.exception.ErrorCode.BALANCE_GAME_SEARCH_BLANK;
import static balancetalk.global.exception.ErrorCode.BALANCE_GAME_SEARCH_LENGTH;

import balancetalk.game.domain.Game;
import balancetalk.game.domain.repository.GameRepository;
import balancetalk.game.dto.SearchGameResponse;
import balancetalk.global.exception.BalanceTalkException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchGameService {

    private final GameRepository gameRepository;

    private static final int MINIMUM_SEARCH_LENGTH = 2;

    public Page<SearchGameResponse> search(String query, Pageable pageable, String sort) {
        List<Game> resultList = new CopyOnWriteArrayList<>();

        // 검색어가 공백이거나 2자 미만일 경우 예외 처리
        if (query.isBlank()) {
            throw new BalanceTalkException(BALANCE_GAME_SEARCH_BLANK);
        }

        if (query.replace(" ", "").length() < MINIMUM_SEARCH_LENGTH) {
            throw new BalanceTalkException(BALANCE_GAME_SEARCH_LENGTH);
        }

        // 1. 완전 일치 검색
        searchExactMatch(query, resultList, sort);

        // 2. 공백 제거한 단어로 완전 일치 검색
        String queryWithoutSpaces = removeSpaces(query);
        searchExactMatch(queryWithoutSpaces, resultList, sort);

        // 3. 자연어 검색
        searchNaturalLanguage(query, resultList, sort);

        List<SearchGameResponse> responses = convertToResponse(resultList);

        // 페이지네이션 적용(페이지 번호, 페이지 크기, 총 게임수는 컨트롤러에서 제공
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());
        return new PageImpl<>(responses.subList(start, end), pageable, responses.size());
    }

    private void searchExactMatch(String query, List<Game> resultList, String sort) {
        List<Game> results = gameRepository.searchExactMatch(query);

        sort(results, sort);
        resultList.addAll(results);
    }

    private void searchNaturalLanguage(String query, List<Game> resultList, String sort) {
        List<Game> results = gameRepository.searchNaturalLanguage(query);

        sort(results, sort);
        resultList.addAll(results);
    }

    private void sort(List<Game> resultList, String sort) {
        if (sort.equals("views")) {
            sortByViews(resultList);
        } else {
            sortByCreatedAt(resultList);
        }
    }

    private String removeSpaces(String query) {
        return query.replaceAll("\\s+", ""); // 모든 공백 제거
    }

    private List<SearchGameResponse> convertToResponse(List<Game> games) {
        return games.stream()
                .map(SearchGameResponse::from)
                .distinct() // 중복 제거
                .toList();
    }

    private void sortByViews(List<Game> resultList) {
        resultList.sort(Comparator
                .comparingLong((Game game) -> game.getGameSet().getViews())
                .thenComparing((Game game) -> game.getGameSet().getCreatedAt()).reversed());
    }

    private void sortByCreatedAt(List<Game> resultList) {
        resultList.sort(Comparator
                .comparing((Game game) -> game.getGameSet().getCreatedAt()).reversed());
    }
}