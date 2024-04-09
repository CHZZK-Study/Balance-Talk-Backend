package balancetalk.global.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {

    private final Long memberId;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, Long memberId) {
        super(username, password, authorities);
        this.memberId = memberId;
    }

    public Long getMemberId() {
        return memberId;
    }
}
