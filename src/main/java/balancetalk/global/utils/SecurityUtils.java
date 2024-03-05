package balancetalk.global.utils;

import static balancetalk.global.exception.ErrorCode.NOT_FOUND_MEMBER;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {

    public static Member getCurrentMember(MemberRepository memberRepository) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_MEMBER));
    }
}
