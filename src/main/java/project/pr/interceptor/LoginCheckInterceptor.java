package project.pr.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String redirectURL = request.getParameter("redirectURL");

        if (redirectURL != null){
            request.getSession().setAttribute("redirectURL" , redirectURL);
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
