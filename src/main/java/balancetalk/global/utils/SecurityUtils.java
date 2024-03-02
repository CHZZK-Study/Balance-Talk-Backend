package balancetalk.global.utils;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static balancetalk.global.exception.ErrorCode.NOT_FOUND_MEMBER;

public class SecurityUtils {
    public static Member getCurrentMember(MemberRepository memberRepository) { // TODO: global static 메서드로 전환?
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_MEMBER));
    }

}
