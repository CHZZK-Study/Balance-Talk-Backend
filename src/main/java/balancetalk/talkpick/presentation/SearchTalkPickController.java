package balancetalk.talkpick.presentation;

import balancetalk.talkpick.application.SearchTalkPickService;
import balancetalk.talkpick.dto.SearchTalkPickResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "talk_pick", description = "톡픽 API")
@RestController
@RequestMapping("/talks/search")
@RequiredArgsConstructor
public class SearchTalkPickController {

    private final SearchTalkPickService searchTalkPickService;

    @Operation(summary = "톡픽 검색", description = "키워드를 통해 톡픽을 검색합니다.")
    @GetMapping
    public Page<SearchTalkPickResponse> searchTalkPicks(@RequestParam final String query,
                                                        Pageable pageable) {
        return searchTalkPickService.searchTalkPicks(query, pageable);
    }
}
