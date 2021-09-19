package project.pr.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import project.pr.domain.Role;
import project.pr.domain.status.Grade;
import project.pr.interceptor.LoginInterceptor;
import project.pr.session.SessionConst;


@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity // Spring Security 설정을 활성화 시켜준다.
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().headers().frameOptions().disable() // h2-console 화면을 사용하기 위해 disable() 설정
                .and()
                    .authorizeRequests()
                        .antMatchers("/**","/","/members/enroll","/css/**" , "/images/**","/js/**" , "/h2-console/**","/login").permitAll()
                        .antMatchers("/home","/members/list","/orders/**","/items/**").hasRole(Role.USER.name())
                        .antMatchers("/api/**").hasRole(Role.ADMIN.name())
                        .anyRequest().authenticated()
                // *!! 에러가 났던곳
                /*
                에러 내용 : 구글, 네이버등의 데이터를 받은 세션은 기존 items , orders 페이지의 권한을 받아서 제대로 들어갔었는데
                직접 회원가입한 세션데이터는 해당 권한을 받지 못해서
                1. 계속 로그인 페이지에 머물었더거나
                2. HELLO SHOP 홈페이지에 들어갔어도 회원목록 , 상품 등록등 페이지들 들어가면 다시 로그인창으로 돌아오는 에러가 발생하였는데

                 .antMatchers("/home","/members/list","/orders/**","/items/**").hasRole(Role.USER.name()) 부분을 주석처리한다음에
                 다시 실행하고 다시 주석을 풀고 진행하였더니 해당 에러가 고쳐졌다. 그 이유는 잘 모르지만 어쩃든 해당 에러가 해결되었다.

                 또한 페이지 내애서 회원가입한 세션데이터는 반드시 LoginInterceptor를 거쳐서 WebConfig 가 존재하여야 하고
                 구글 , 네이버 등 다른 곳에서 데이터를 받아오는 세션은 SecurityConfig 가 따로 존재해서 관리해야 한다는 것을 알게 되었다.

                 그래야만 해당 세션의 권한이 서로 맞게 부여되기 때문이다.
                 */
                .and()
                    .oauth2Login().loginPage("/login")
                .and()
                    .formLogin().loginPage("/loginForm.html").usernameParameter("loginId").passwordParameter("password")
                // 지금 내 커스텀 로그인의 url 이 /login 이므로 설정해야한다.
                // 만약에 설정하지 않으면 OAuth2가 기본으로 제공하는 /login 페이지로 이동하기 때문에
                .and()
                    .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/").invalidateHttpSession(true)
                .and()
                    .oauth2Login().userInfoEndpoint().userService(customOAuth2UserService);
    }






}
