package project.pr.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import project.pr.domain.Member;

import java.util.Map;

@Slf4j
@Getter
public class OAuthAttributes {

    private Map<String , Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    public static OAuthAttributes of(String registrationId , String userNameAttributeName , Map<String , Object> attributes){

        log.info("==============================================");
        log.info("{}",registrationId);

        // 네이버 추가
        if ("naver".equals(registrationId)){
            return ofNaver("id",attributes);
        }
        // 카카오 추가
        if ("kakao".equals(registrationId)){
            return ofKakao("id",attributes);
        }

        // 구글
        return ofGoogle(userNameAttributeName,attributes);
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("kakao_account");
        // kakao는 kakao_account에 유저정보가 있다. (email)
        Map<String, Object> profile = (Map<String, Object>) response.get("profile");
        // kakao_account안에 또 profile이라는 JSON객체가 있다. (nickname, profile_image)

        System.out.println("profile.toString() = " + profile.toString());
        
        return OAuthAttributes.builder()
                .name((String) profile.get("nickname"))
                .email((String) profile.get("email"))
                .picture((String) profile.get("profile_image_url"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        OAuthAttributes build = OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .picture((String) response.get("profile_image"))
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
        return build;
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        OAuthAttributes build = OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
        return build;
    }

    public Member toEntity(String name , String email){
        Member member = new Member(name, email);
        return member;
    }



}
