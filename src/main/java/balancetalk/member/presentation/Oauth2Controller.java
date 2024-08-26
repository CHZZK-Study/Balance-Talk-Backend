package balancetalk.member.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2")
@Validated
@Tag(name = "oauth2", description = "소셜 로그인 API")
public class Oauth2Controller {

    @GetMapping("/naver")
    @Operation(summary = "소셜 로그인_네이버", description = "네이버 소셜 로그인 URL을 리다이렉트 합니다.")
    public RedirectView loginToNaver() {
        return new RedirectView("/oauth2/authorization/naver");
    }

    @GetMapping("/kakao")
    @Operation(summary = "소셜 로그인_카카오", description = "카카오 소셜 로그인 URL을 리다이렉트 합니다.")
    public RedirectView loginToKakao() {
        return new RedirectView("/oauth2/authorization/kakao");
    }

    @GetMapping("/google")
    @Operation(summary = "소셜 로그인_구글", description = "구글 소셜 로그인 URL을 리다이렉트 합니다.")
    public RedirectView loginToGoogle() {
        return new RedirectView("/oauth2/authorization/kakao");
    }
}
