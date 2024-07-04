package balancetalk.bookmark.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookmarks/talks/{talkPickId}")
@Tag(name = "bookmark", description = "북마크 API")
public class BookmarkTalkPickController {

    @Operation(summary = "톡픽 북마크 추가", description = "북마크에 톡픽을 추가합니다.")
    @PostMapping
    public void bookmarkTalkPick(@PathVariable Long talkPickId) {
    }

    @Operation(summary = "톡픽 북마크 제거", description = "북마크에서 톡픽을 제거합니다.")
    @DeleteMapping
    public void deleteBookmarkTalkPick(@PathVariable Long talkPickId) {
    }
}
