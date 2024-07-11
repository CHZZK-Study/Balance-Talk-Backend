package balancetalk.global.oauth2.service;

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

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.error("Loading user: {}", oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        Oauth2Response oauth2Response = null;

        if (registrationId.equals("naver")) {
            oauth2Response = new NaverResponse(oAuth2User.getAttributes());
        }

        else if (registrationId.equals("google")) {
            oauth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }

        else if (registrationId.equals("kakao")) {
            oauth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }

        else {
            return null;
        }

        String username = oauth2Response.getProvider() + " " + oauth2Response.getProviderId();
        Member findMember = memberRepository.findByUsername(username);

        if (findMember == null) {
            Oauth2Dto oauth2Dto = Oauth2Dto.builder()
                    .name(oauth2Response.getName())
                    .email(oauth2Response.getEmail())
                    .username(username)
                    .role(Role.USER)
                    .build();

            Member newMember = oauth2Dto.toEntity();
            memberRepository.save(newMember);

            return new CustomOAuth2User(oauth2Dto);
        }

        else { // 회원이 존재할 떄
            findMember.updateNickname(oauth2Response.getName());
            Oauth2Dto oauth2Dto = Oauth2Dto.builder()
                    .name(findMember.getNickname())
                    .email(findMember.getEmail())
                    .username(findMember.getUsername())
                    .role(Role.USER)
                    .build();
            return new CustomOAuth2User(oauth2Dto);
        }
    }
}
