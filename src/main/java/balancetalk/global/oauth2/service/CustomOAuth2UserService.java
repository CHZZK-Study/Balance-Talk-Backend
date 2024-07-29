package balancetalk.global.oauth2.service;

import static balancetalk.global.config.SecurityConfig.*;
import balancetalk.global.oauth2.dto.CustomOAuth2User;
import balancetalk.global.oauth2.dto.GoogleResponse;
import balancetalk.global.oauth2.dto.KakaoResponse;
import balancetalk.global.oauth2.dto.NaverResponse;
import balancetalk.global.oauth2.dto.Oauth2Dto;
import balancetalk.global.oauth2.dto.Oauth2Response;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final String OAUTH2_PASSWORD = "OAUTH2_PASSWORD";
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("Loading user: {}", oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        Oauth2Response oauth2Response = switch (registrationId) {
            case "naver" -> new NaverResponse(oAuth2User.getAttributes());
            case "google" -> new GoogleResponse(oAuth2User.getAttributes());
            case "kakao" -> new KakaoResponse(oAuth2User.getAttributes());
            default -> null;
        };

        String username = oauth2Response.getProvider() + " " + oauth2Response.getProviderId();
        Member findMember = memberRepository.findByUsername(username);

        if (findMember == null) {
            String encodedPassword = passwordEncoder().encode(OAUTH2_PASSWORD);
            Oauth2Dto oauth2Dto = Oauth2Dto.builder()
                    .name(oauth2Response.getEmail())
                    .email(oauth2Response.getEmail())
                    .username(username)
                    .role(Role.USER)
                    .password(encodedPassword)
                    .build();

            Member newMember = oauth2Dto.toEntity();
            memberRepository.save(newMember);
            newMember.updateNickname(hideNickname(newMember.getNickname()));
            return new CustomOAuth2User(oauth2Dto);
        }

        else { // 회원이 존재할 떄
            Oauth2Dto oauth2Dto = Oauth2Dto.builder()
                    .name(findMember.getNickname())
                    .email(findMember.getEmail())
                    .username(findMember.getUsername())
                    .role(findMember.getRole())
                    .build();
            return new CustomOAuth2User(oauth2Dto);
        }
    }

    private String hideNickname(String nickname) {
        StringBuilder sb = new StringBuilder(nickname);
        for (int i = 3; i < nickname.length(); i++) {
            if (nickname.charAt(i) == '@') {
                break;
            }
            sb.setCharAt(i, '*');
        }
        return sb.toString();
    }
}
