package balancetalk.like.presentation;

import balancetalk.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/talks/{talkPickId}/like")
@Tag(name = "talk_pick", description = "톡픽 API")
public class LikeTalkPickController {

    @Operation(summary = "톡픽 좋아요", description = "톡픽 좋아요를 활성화합니다.")
    @PostMapping
    public ApiResponse<Object> likeTalkPick(@PathVariable final Long talkPickId) {
        return ApiResponse.ok();
    }

    @Operation(summary = "톡픽 좋아요 취소", description = "톡픽 좋아요를 취소합니다.")
    @DeleteMapping
    public ApiResponse<Object> unlikeTalkPick(@PathVariable final Long talkPickId) {
        return ApiResponse.ok();
    }
}
