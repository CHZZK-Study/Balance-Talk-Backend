package balancetalk.global.config;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_MEMBER));

        List<GrantedAuthority> authorities = Collections.emptyList();

        CustomUserDetails userDetails = new CustomUserDetails(
                member.getEmail(),
                member.getPassword(),
                authorities,
                member.getId() // memberId를 추가
        );
        return userDetails;
    }
}
