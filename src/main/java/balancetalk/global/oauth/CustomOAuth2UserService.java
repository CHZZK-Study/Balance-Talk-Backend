package balancetalk.global.oauth;

import balancetalk.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // OAuth2UserService를 사용하여 사용자 정보를 가져옴
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 추가적인 사용자 정보 처리 로직
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 소셜 로그인 제공자에 따라 사용자 정보를 가공
        if ("kakao".equals(registrationId)) {
            attributes = extractKakaoAttributes(attributes);
        } else if ("naver".equals(registrationId)) {
            attributes = extractNaverAttributes(attributes);
        }

        return new DefaultOAuth2User(
                Collections.singleton(new OAuth2UserAuthority(attributes)),
                attributes,
                "email"
        );
    }

    private Map<String, Object> extractKakaoAttributes(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = (String) kakaoAccount.get("email");
        String nickname = (String) ((Map<String, Object>) kakaoAccount.get("profile")).get("nickname");

        Map<String, Object> newAttributes = new HashMap<>();
        newAttributes.put("email", email);
        newAttributes.put("name", nickname);
        return newAttributes;
    }

    private Map<String, Object> extractNaverAttributes(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        String email = (String) response.get("email");
        String nickname = (String) response.get("nickname");

        Map<String, Object> newAttributes = new HashMap<>();
        newAttributes.put("email", email);
        newAttributes.put("name", nickname);
        return newAttributes;
    }
}
