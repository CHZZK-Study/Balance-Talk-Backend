package balancetalk.module.member.presentation;

import balancetalk.module.member.application.MemberService;
import balancetalk.module.member.dto.JoinDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/join")
    public String joinMember(@Valid @RequestBody JoinDto joinDto, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        joinDto.setIp(ipAddress);
        memberService.join(joinDto);
        return "회원가입이 정상적으로 처리되었습니다.";
    }
}
