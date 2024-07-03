package balancetalk.talkpick.presentation;

import balancetalk.talkpick.dto.TalkPickDetailResponse;
import balancetalk.talkpick.dto.TalkPickRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import static balancetalk.talkpick.dto.TalkPickDto.CreateTalkPickRequest;
import static balancetalk.vote.domain.VoteOption.A;

@RestController
@RequestMapping("/talks")
@Tag(name = "talk_pick", description = "톡픽 API")
public class TalkPickController {

    @Operation(summary = "톡픽 생성", description = "톡픽을 생성합니다.")
    @PostMapping
    public void createTalkPick(@RequestBody final CreateTalkPickRequest request) {
    }

    @Operation(summary = "톡픽 상세 조회", description = "톡픽 상세 페이지를 조회합니다.")
    @GetMapping("/{talkPickId}")
    public TalkPickDetailResponse findTalkPickDetail(@PathVariable final Long talkPickId) {
        return new TalkPickDetailResponse(1L, "제목", "내용", "요약", "O", "X", 142L, 12L, true, false, true, A);
    }

    @Operation(summary = "톡픽 수정", description = "톡픽을 수정합니다.")
    @PutMapping("/{talkPickId}")
    public void updateTalkPick(@PathVariable final Long talkPickId, @RequestBody final TalkPickRequest request) {
    }

    @Operation(summary = "톡픽 삭제", description = "톡픽을 삭제합니다.")
    @DeleteMapping("/{talkPickId}")
    public void deleteTalkPick(@PathVariable final Long talkPickId) {
    }
}
