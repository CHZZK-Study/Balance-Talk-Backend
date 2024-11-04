package balancetalk.member.presentation;

import balancetalk.global.utils.AuthPrincipal;
import balancetalk.member.application.MemberService;
import balancetalk.member.dto.ApiMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static balancetalk.member.dto.MemberDto.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Validated
@Tag(name = "member", description = "회원 API")
public class MemberController {

    private final MemberService memberService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/join")
    @Operation(summary = "회원 가입", description = "닉네임, 이메일, 비밀번호를 입력하여 회원 가입을 한다.")
    public void join(@Valid @RequestBody final JoinRequest joinRequest) {
        memberService.join(joinRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "회원 가입 한 이메일과 패스워드를 사용하여 로그인 한다.")
    public String login(@Valid @RequestBody final LoginRequest loginRequest, HttpServletResponse response) {
        return memberService.login(loginRequest, response);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{memberId}")
    @Operation(summary = "단일 회원 조회", description = "memberId와 일치하는 회원 정보를 조회한다.")
    public MemberResponse findMemberInfo(@PathVariable("memberId") Long memberId) {
        return memberService.findById(memberId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @Operation(summary = "전체 회원 조회", description = "모든 회원 정보를 조회한다.")
    public List<MemberResponse> findAllMemberInfo() {
        return memberService.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/nickname", consumes = "text/plain")
    @Operation(summary = "회원 닉네임 수정", description = "회원 닉네임을 수정한다.")
    public void updateNickname(@Valid @NotBlank @RequestBody @Size(min = 2, max = 10) final String newNickname,
                               @Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {
        memberService.updateNickname(newNickname, apiMember);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/image", consumes = "text/plain")
    @Operation(summary = "회원 이미지 변경", description = "회원 프로필 이미지를 변경한다.")
    public void updateImage(@RequestBody final String profileImgUrl,
                            @Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {
        memberService.updateImage(profileImgUrl, apiMember);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/delete")
    @Operation(summary = "회원 삭제", description = "회원 정보를 삭제한다.")
    public void deleteMember(@Valid @RequestBody final LoginRequest loginRequest,
                             @Parameter(hidden = true) @AuthPrincipal ApiMember apiMember) {
        memberService.delete(loginRequest, apiMember);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그인 된 회원을 로그 아웃한다.")
    public void logout() {
        memberService.logout();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/duplicate")
    @Operation(summary = "닉네임 중복 검증", description = "중복된 닉네임이 존재하는지 체크한다.")
    public void verifyNickname(@RequestParam @NotBlank
                               @Size(min = 2, max = 10) String nickname) {
        memberService.verifyNickname(nickname);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping("/reissue")
    @Operation(summary = "액세스 토큰 재발급", description = "만료된 액세스 토큰을 재발급 받는다.")
    public String reissueAccessToken(HttpServletRequest request) {
        return memberService.reissueAccessToken(request);
    }
}
