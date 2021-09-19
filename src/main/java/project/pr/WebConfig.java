package project.pr;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import project.pr.auth.LoginUserArgumentResolver;
import project.pr.interceptor.LogInterceptor;
import project.pr.interceptor.LoginCheckInterceptor;
import project.pr.interceptor.LoginInterceptor;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {



    // interceptor 를 등록하기 위해서는
    // WebMvcConfigurer 에서 addInterceptors 를 오버라이딩 해준다.

    // 또한 스프링빈에 등록해야 하므로 WebMvcConfigurer를 implements 한 WebConfig 를
    // @Configuration 을 등록해서 스프링 빈에 싱글톤 방식으로 등록한다.

    private final LoginUserArgumentResolver loginUserArgumentResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new LogInterceptor())
        // addInterceptor 에는 우리가 만든 LogInterceptor() 를 넣는다.
                .order(2)
        // order 에는 숫자를 넣어서 interceptor가 진행될 순서를 결정한다. 모든 로그를 볼것이기 때문에 1로 설정
                .addPathPatterns("/**")
        // addPathPatterns 에는 uri 경로를 지정하며 /**는 /아래의 모든경로이므로
        // 즉, 모든경로에 대해 해당 interceptor 가 등록된다.
                .excludePathPatterns("/css/**","/*.ico","/error");
        // excludePathPatterns 에는 해당 interceptor가 나오지 않도록 설정하는 uri를 지정할 수 있다.

        // 이제는 로그인 사용자를 체크하는 interceptor를 등록해야한다.
        // 생각해보면 로그인되지 않더라도 위처럼 html의 css 이나 .ico , errors 같은 요청들은 진행되어야 할것이다.
        // 또한 로그인하지 않는 사용자도 회원가입을 할수 있어야 하며 , 로그인폼에 들어갈 수있어야 한다.

        registry.addInterceptor(new LoginInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**","/*.ico","/login","/members/enroll","/","/home");

        // 현재 registry 옵션이 적용되지 않기 때문에 새롭게 설정한다.
        //registry.addInterceptor(new LoginCheckInterceptor()).addPathPatterns("/login");
        // 새롭게 LoginCheckInterceptor 클래스를 만든다.


        // 그래서 위와 비슷하게 addInterceptor 에는 LogInterceptor 대신 LoginInterceptor 를 넣고
        // excludePathPatterns 에는 /members/enroll (회원가입창)과 /login (로그인폼) 만 LoginInterceptor가 호출되지 않도록 설정한다.
    }


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        //  resolvers.add(loginUserArgumentResolver);
        // 해당 ArgumentResolver 를 add 함으로써 위의 Interceptor 설정이 씹힌다는 표현으로 작동하지 않는다.
        // 즉 현재 SecurityConfig 에서의 filter가 적용되고 위 addInterceptor의 registry filter 설정이 적용되지 않음
        // 그래서 설정을 변경 해야 할 것 같다.
    }
}
