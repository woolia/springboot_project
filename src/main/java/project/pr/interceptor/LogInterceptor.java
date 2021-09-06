package project.pr.interceptor;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    public static final String LOG_ID = "logId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        String logId = UUID.randomUUID().toString();

        request.setAttribute(LOG_ID,logId);

        if(handler instanceof HandlerMethod){
            HandlerMethod hm = (HandlerMethod) handler;
        }
        return true;
        // Filter에서는 chain.doFilter(request,response) 를 연결할때 return 의 여부로 체크하는데
        // interceptor에서는 return true 면 다음 Servlet Controller를 호출하고
        // return false 이면 호출하지 않는 것으로 결정된다.

        // 여기서는 그냥 Log 정보만 계속 확인하는 것이기 때문에 return true 로 다음 Servlet Controller가
        // 호출되도록 설정한다.
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle = {}" , modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        String requestURI = request.getRequestURI();
        String logId = (String) request.getAttribute(LOG_ID);
        // request에 담긴 LOG_ID 정보의 uuid를 가져옴

        log.info("RESPONSE [{}] [{}] [{}]" , logId , requestURI ,handler);
//
//        if(ex != null){
//            log.error("error ", ex);
//        }

    }
}
