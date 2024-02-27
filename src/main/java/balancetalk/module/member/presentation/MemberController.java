package balancetalk.module.member.presentation;

import balancetalk.global.jwt.JwtTokenProvider;
import balancetalk.module.member.application.MemberService;
import balancetalk.module.member.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "member", description = "회원 API")
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/join")
    @Operation(summary = "회원 가입", description = "닉네임, 이메일, 비밀번호를 입력하여 회원 가입을 한다.")
    public String join(@Valid @RequestBody JoinDto joinDto, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        joinDto.setIp(ipAddress);
        memberService.join(joinDto);
        return "회원가입이 정상적으로 처리되었습니다.";
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "회원 가입 한 이메일과 패스워드를 사용하여 로그인 한다.")
    public LoginSuccessDto login(@Valid @RequestBody LoginDto loginDto) {
        return memberService.login(loginDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/find")
    @Operation(summary = "단일 회원 조회", description = "해당 jwt 토큰 값과 일치하는 회원 정보를 조회한다.")
    public MemberResponseDto findMemberInfo(HttpServletRequest request) {
        return memberService.findById(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/findAll")
    @Operation(summary = "전체 회원 조회", description = "모든 회원 정보를 조회한다.")
    public List<MemberResponseDto> findAllMemberInfo() {
        return memberService.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/update/nickname")
    @Operation(summary = "회원 닉네임 수정", description = "회원 닉네임을 수정한다.")
    public String updateNickname(@Valid @RequestBody NicknameUpdate nicknameUpdate, HttpServletRequest request) {
        memberService.updateNickname(nicknameUpdate, request);
        return "회원 닉네임이 변경되었습니다.";
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/update/password")
    @Operation(summary = "회원 비밀번호 수정", description = "회원 패스워드를 수정한다.")
    public String updatePassword(@Valid @RequestBody PasswordUpdate passwordUpdate, HttpServletRequest request) {
        memberService.updatePassword(passwordUpdate, request);
        return "회원 비밀번호가 변경되었습니다.";
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/delete")
    @Operation(summary = "회원 삭제", description = "해당 id값과 일치하는 회원 정보를 삭제한다.")
    public String deleteMember(@Valid @RequestBody LoginDto loginDto, HttpServletRequest request) {
        memberService.delete(loginDto, request);
        return "회원 탈퇴가 정상적으로 처리되었습니다.";
    }

}
