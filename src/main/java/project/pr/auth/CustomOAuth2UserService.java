package project.pr.auth;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import project.pr.auth.dto.OAuthAttributes;
import project.pr.auth.dto.SessionUser;
import project.pr.domain.Member;
import project.pr.repository.db.MemberRepository;
import project.pr.session.SessionConst;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest,OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        /*
        DefaultOAuth2UserService는 OAuth2UserService의 구현체입니다.
        해당 클래스를 이용해서 userRequest에 있는 정보를 빼낼 수 있습니다.
         */

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // registrationId는 로그인을 진행중인 서비스를 구분하는 코드이다. 구글,네이버,카카오 등등

        String userNameAttributeName
                = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        // userNameAttributeName 는 OAuth2 로그인 진행시에 키가 되는 필드값이다 구글은 기본적으로 코드를 지원하지만 네이버 , 카카오등은 지원하지 않는다.
        // 구글의 기본코드는 "sub" 이다
        // 이후에 구글로그인 , 네이버로그인을 동시 지원할 때 사용된다.

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        Member loginMember = saveOrUpdate(attributes);
        httpSession.setAttribute(SessionConst.LOGIN_MEMBER , loginMember);

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(loginMember.getRole().getKey())),
                attributes.getAttributes(), attributes.getNameAttributeKey());
    }

    private Member saveOrUpdate(OAuthAttributes attributes) {
        log.info("{}",memberRepository.findSessionByEmail(attributes.getEmail()));
        Member user = memberRepository.findSessionByEmail(attributes.getEmail()).map(member -> member.otherLoginMemberUpdate(attributes.getName(), attributes.getEmail()))
                .orElse(attributes.toEntity(attributes.getName(), attributes.getEmail()));
        return memberRepository.save(user);
    }
}
