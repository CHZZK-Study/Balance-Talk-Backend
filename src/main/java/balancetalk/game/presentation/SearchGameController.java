package balancetalk.game.presentation;

import balancetalk.game.application.SearchGameService;
import balancetalk.game.dto.SearchGameResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "search", description = "밸런스게임 검색 API")
public class SearchGameController {

    private final SearchGameService searchGameService;

    @Operation(summary = "밸런스게임 검색", description = "밸런스게임을 검색합니다. (정렬 기준 : views, createdAt)")
    @GetMapping("/search/game")
    public Page<SearchGameResponse> searchTalkPicks(@RequestParam("query") String query,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "9", required = false) int size,
                                                    @RequestParam(defaultValue = "views") String sort) {

        Pageable pageable = PageRequest.of(page, size);
        return searchGameService.search(query, pageable, sort);
    }
}