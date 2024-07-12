package balancetalk.global.oauth2.dto;

import java.util.Map;

public class KakaoResponse implements Oauth2Response {

    private final Map<String, Object> attributes;
    private final Map<String, Object> kakaoAccount;
    private final Map<String, Object> kakaoProfile;

    public KakaoResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        return kakaoAccount.get("email").toString();
    }

    @Override
    public String getName() {
        return kakaoProfile.get("nickname").toString();
    }


    public String getProfileImageUrl() {
        Object profileImageUrl = kakaoProfile.get("profile_image_url");
        return profileImageUrl != null ? profileImageUrl.toString() : "No profile image URL provided";
    }
}
