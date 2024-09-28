package balancetalk.game.presentation;

import balancetalk.game.application.SearchGameService;
import balancetalk.game.dto.SearchGameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchGameController {

    private final SearchGameService searchGameService;

    @GetMapping("/search/game")
    public Page<SearchGameResponse> searchTalkPicks(@RequestParam("query") String query,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "9", required = false) int size,
                                                    @RequestParam(defaultValue = "views") String sort) {

        Pageable pageable = PageRequest.of(page, size);
        return searchGameService.search(query, pageable, sort);
    }
}