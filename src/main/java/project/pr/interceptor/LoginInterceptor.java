package project.pr.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import project.pr.session.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {


    // 로그인 요청은 preHandle 메서드만 있으면 된다.
    // 다른 postHandle , afterCompletion 등은 로그인에 대한 필터에 적용되지 않는다.


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("========== postHandle =========");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("============== afterCompletion ============");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();

        log.info("로그인 인증 시작" , requestURI);

        HttpSession session = request.getSession(false);

        if(session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null){
            log.info("인증되지 않는 사용자");

            // session이 존재하지 않고 session안에 LOGIN_MEMBER 라는 상수값이 없으면 로그인 멤버가 아니다.
            // 따라서 로그인처리가 되도록 로그인 창으로 이동시킨다.

            response.sendRedirect("/login?redirectURL="+requestURI);
            // response.sendRedirect() 는 서버에서 클라이언트에게 응답하는 것으로
            // 해당 클라이언트를 sendRedirect() 로 해당 url로 이동시킨다.

            // 또한 login이 진행되면 parameter 값으로 requestURI가 담긴 redirectURL을 파라미터로 보내서
            // /login 이 있는 Controller에서 redirectURL 파라미터를 사용해서 다시 해당 url로 보내주는 로직을 작성할 수 있다.

            // 해당 파라미터는 GetMapping의 /login 이 아닌 PostMapping의 /login 에서 받아서 바로 보내주도록 한다.

            return false;
            // 여기서 false 는 더이상 진행하지 않겠다는 의미로
            // 어차피 인증되지 않는 사용자이기 때문에 사용자를 체크하는 더이상 다음 Controller를 호출할 필요가 없다
            // 그래서 false를 하는것
        }

        return true;
        // 여기서는 session도 존재하고 session의 LOGIN_MEMBER 상수값이 존재하는 로그인 회원이기 때문에
        // return true 로 다음 Controller를 호출하도록 한다.
    }
}
