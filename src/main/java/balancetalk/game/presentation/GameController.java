package balancetalk.game.presentation;

import balancetalk.game.dto.GameRequest;
import balancetalk.game.dto.GameResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("games")
@Tag(name = "game", description = "주제별 밸런스 게임 API")
public class GameController {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "주제별 밸런스 게임 생성")
    public GameResponse createBalanceGame(@RequestBody final GameRequest request) {
        return GameResponse.builder().build();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/best")
    @Operation(summary = "인기 게시글 조회", description = "요새 인기 있는 주제별 밸런스 게임 목록 조회")
    public List<GameResponse> findBestPosts() {
        GameResponse gameResponses = GameResponse.builder().build();
        return Collections.singletonList(gameResponses);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/new")
    @Operation(summary = "새로운 게시글 목록 조회", description = "새로 나온 밸런스 게임 목록 조회")
    public List<GameResponse> findNewPosts() {
        GameResponse gameResponses = GameResponse.builder().build();
        return Collections.singletonList(gameResponses);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{postId}")
    @Operation(summary = "밸런스 게임 조회", description = "주제별 밸런스 게임 단건 조회")
    public GameResponse findPost(@PathVariable Long postId) {
        return GameResponse.builder().build();
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{postId}")
    @Operation(summary = "밸런스 게임 수정", description = "밸런스 게임 게시글을 수정한다")
    public GameResponse updatePost(@PathVariable Long postId, @RequestBody final GameRequest request) {
        return GameResponse.builder().build();
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{postId}")
    @Operation(summary = "밸런스 게임 삭제", description = "밸런스 게임 게시글을 삭제한다")
    public String updatePost(@PathVariable Long postId) {
        return "게시글이 삭제되었습니다.";
    }
}
