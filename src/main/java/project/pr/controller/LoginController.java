package project.pr.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.pr.auth.dto.SessionUser;
import project.pr.controller.form.LoginForm;
import project.pr.controller.form.MemberEnrollForm;
import project.pr.domain.Member;
import project.pr.service.MemberService;
import project.pr.session.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final MemberService memberService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute LoginForm loginForm , HttpServletRequest request){

        return "login/loginForm";
    }

    //@PostMapping("/login")
    public String login(@Validated @ModelAttribute LoginForm loginForm , BindingResult bindingResult ,
                        HttpServletRequest request , Model model){

        log.info("{} , {}" , loginForm.getLoginId() , loginForm.getPassword());

        Member validatedMember = memberService.findLoginId(loginForm.getLoginId());

        if(validatedMember == null || ! validatedMember.getPassword().equals(loginForm.getPassword())){
            bindingResult.reject("nonMatchLogin" , null);
        }

        if(bindingResult.hasErrors()){
            return "login/loginForm";
        }

        // 로그인 성공처리

        Member loginMember = memberService.findLoginMember(loginForm.getLoginId(), loginForm.getPassword());
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER , loginMember);

        log.info("{} , {} , {}" , loginMember.getName() , loginMember.getLoginId() , loginMember.getPassword());

        return "redirect:/home";
    }


    // login2 에서는 LoginInterceptor에서 넘어온 redirectURL 파라미터로 다시 해당 url로 redirect 해주는
    // 역할을 추가한다.
    @PostMapping("/login")
    public String login2(@Validated @ModelAttribute LoginForm loginForm , BindingResult bindingResult ,
                        HttpServletRequest request , Model model,
                        @RequestParam(defaultValue = "/") String redirectURL){

        log.info("로그인 세션 : {}" , request.getSession());
        log.info("{} , {}" , loginForm.getLoginId() , loginForm.getPassword());

        Member validatedMember = memberService.findLoginId(loginForm.getLoginId());

        if(validatedMember == null || ! validatedMember.getPassword().equals(loginForm.getPassword())){
            bindingResult.reject("nonMatchLogin" , null);
        }

        if(bindingResult.hasErrors()){
            log.info("에러가 있습니다. ");
            return "login/loginForm";
        }

        // 로그인 성공처리

        Member loginMember = memberService.findLoginMember(loginForm.getLoginId(), loginForm.getPassword());
        SessionUser sessionUser = new SessionUser(loginMember);
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER , loginMember);

        log.info("{} , {} , {}" , loginMember.getName() , loginMember.getLoginId() , loginMember.getPassword());


        // 새롭게 추가한 부분
        // 현재 나의 로직에서는 / 기본경로는 회원가입 과 로그인을 담당하는 html view 이고 /home 은 상품 , 회원목록 , 주문등을 처리하는 view 이므로
        // 만약에 파라미터로 redirectURL 값이 넘어오게 되었을 때는 redirectURL 값이 있기 때문에
        // !redirectURL.equals("") 로 redirectURL 가 기본경로가 아닐때 redirect:/redirectURL 로 이동하도록 설정하였다.

        Object attribute = session.getAttribute(SessionConst.LOGIN_MEMBER);

        if (!redirectURL.equals("/")){
            log.info("redirect 쪽 " , redirectURL);
           return "redirect:"+redirectURL ;
        }
        // 만약에 redirectURL 이 없다면 일반 로그인창으로 들어온 경우이기 때문에 그냥 /home으로 보낸다.

        log.info("/home 으로 돌아간다 .");

        return "redirect:/";


    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest request){

        log.info("로그아웃 세션 : {}" , request.getSession());

        HttpSession session = request.getSession(false);
        // 여기서 false 라고 하는 이유는 request.getSession(true) 라고 하면 새로운 세션을 만든다.
        // 우리가 할것은 기존의 세션을 지우는 것이지 새로운 세션을 새롭게 만드는 것이 아니기 때문에 false 라고 지정한다.
        // 참고로 request.getSession() 기본값은 true 이다.

        // 또한 여기서 파라미터로 넘어오는 request는 로그인 된 상태라면 회원가입된 멤버의 세션일 것이다.

        // 더 얘기하면 클라이언트의 해당 세션에는 아이디와 비밀번호가 들어있고
        // 개인정보(이메일 , 전화번호 등은)들은 서버안의 세션에 들어가 있다.

        if(session != null){
            // session이 null 이 아니면 회원값을 가진 세션으로 초기화를 해준다.


            // invalidate() 하면 session이 초기화됨
            session.invalidate();
        }

        return "redirect:/";
    }



}
