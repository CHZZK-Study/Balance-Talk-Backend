package balancetalk.module.member.domain;

import balancetalk.game.domain.Game;
import balancetalk.game.domain.GameBookmark;
import balancetalk.global.common.BaseTimeEntity;
import balancetalk.like.domain.Like;
import balancetalk.module.file.domain.File;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.TalkPickFile;
import balancetalk.vote.domain.Vote;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Size(min = 2, max = 10)
    @Column(nullable = false, length = 10, unique = true)
    private String nickname;

    @NotBlank
    @Size(max = 30)
    @Email(regexp = "^[a-zA-Z0-9._%+-]{1,20}@[a-zA-Z0-9.-]{1,10}\\.[a-zA-Z]{2,}$")
    @Column(nullable = false, length = 30, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "member")
    private List<Game> games = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<GameBookmark> gameBookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<TalkPick> talkPicks = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Like> talkPickLikes = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<TalkPickFile> talkPickFiles = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id")
    private File profilePhoto;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateImage(File profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
}
