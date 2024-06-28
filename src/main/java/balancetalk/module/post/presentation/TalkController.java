package balancetalk.module.post.presentation;

import balancetalk.game.dto.GameResponse;
import balancetalk.module.post.dto.TalkRequest;
import balancetalk.module.post.dto.TalkResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("talks")
@Tag(name = "talk", description = "오늘의 톡픽 API")
public class TalkController {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "오늘의 톡픽 생성")
    public TalkResponse createTalkPick(@RequestBody final TalkRequest request) {
        return TalkResponse.builder().build();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{postId}/likes")
    @Operation(summary = "오늘의 톡픽 추천", description = "post-id에 해당하는 게시글에 추천을 누른다.")
    public String likePost(@PathVariable Long postId) {
        return "요청이 정상적으로 처리되었습니다.";
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{postId}/likes")
    @Operation(summary = "오늘의 톡픽 추천 취소", description = "post-id에 해당하는 게시글에 누른 추천을 취소한다.")
    public String cancelLikePost(@PathVariable Long postId) {
        return "요청이 정상적으로 처리되었습니다.";
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{postId}")
    @Operation(summary = "오늘의 톡픽 조회", description = "오늘의 톡픽 단건 조회")
    public TalkResponse findPost(@PathVariable Long postId) {
        return TalkResponse.builder().build();
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{postId}")
    @Operation(summary = "오늘의 톡픽 수정", description = "오늘의 톡픽 게시글을 수정한다")
    public GameResponse updatePost(@PathVariable Long postId, @RequestBody final TalkRequest request) {
        return GameResponse.builder().build();
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{postId}")
    @Operation(summary = "오늘의 톡픽 삭제", description = "오늘의 톡픽 게시글을 삭제한다")
    public String updatePost(@PathVariable Long postId) {
        return "게시글이 삭제되었습니다.";
    }
}
