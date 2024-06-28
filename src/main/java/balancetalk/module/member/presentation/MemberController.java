package balancetalk.module.member.presentation;

import balancetalk.module.member.application.MemberService;
import balancetalk.module.member.dto.JoinRequest;
import balancetalk.module.member.dto.LoginRequest;
import balancetalk.module.member.dto.MemberResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public String join(@Valid @RequestBody JoinRequest joinRequest) {
        memberService.join(joinRequest);
        return "회원가입이 정상적으로 처리되었습니다.";
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "회원 가입 한 이메일과 패스워드를 사용하여 로그인 한다.")
    public String login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
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
    public String updateNickname(@Valid @NotBlank @RequestBody @Size(min = 2, max = 10) String newNickname, HttpServletRequest request) {
        // TODO: RequestBody 빈 값일 때 에러체킹 x
        memberService.updateNickname(newNickname, request);
        return "회원 닉네임이 변경되었습니다.";
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/password", consumes = "text/plain")
    @Operation(summary = "회원 비밀번호 수정", description = "회원 패스워드를 수정한다.")
    public String updatePassword(@RequestBody @Size(min = 10, max = 20)
                                 @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{10,20}$")
                                 String newPassword, HttpServletRequest request) {
        // TODO: RequestBody 빈 값일 때 에러체킹 x
        memberService.updatePassword(newPassword, request);
        return "회원 비밀번호가 변경되었습니다.";
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/image", consumes = "text/plain")
    @Operation(summary = "회원 이미지 변경", description = "회원 프로필 이미지를 변경한다.")
    public String updateImage(@RequestBody String storedFileName, HttpServletRequest request) {
        memberService.updateImage(storedFileName, request);
        return "회원 이미지가 변경되었습니다.";
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    @Operation(summary = "회원 삭제", description = "회원 정보를 삭제한다.")
    public String deleteMember(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        memberService.delete(loginRequest, request);
        return "회원 탈퇴가 정상적으로 처리되었습니다.";
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그인 된 회원을 로그 아웃한다.")
    public String logout() {
        memberService.logout();
        return "로그아웃이 정상적으로 처리되었습니다.";
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/duplicate")
    @Operation(summary = "닉네임 중복 검증", description = "중복된 닉네임이 존재하는지 체크한다.")
    public String verifyNickname(@RequestParam @NotBlank
                                 @Size(min = 2, max = 10) String nickname) {
        memberService.verifyNickname(nickname);
        return "사용 가능한 닉네임 입니다.";
    }

    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping("/reissue")
    @Operation(summary = "액세스 토큰 재발급", description = "만료된 액세스 토큰을 재발급 받는다.")
    public String reissueAccessToken(HttpServletRequest request) {
        return memberService.reissueAccessToken(request);
    }
}
