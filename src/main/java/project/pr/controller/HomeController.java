package project.pr.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import project.pr.domain.*;
import project.pr.domain.status.Grade;
import project.pr.domain.status.ItemType;
import project.pr.service.ItemService;
import project.pr.service.MemberService;
import project.pr.session.SessionConst;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@RequestMapping
@RequiredArgsConstructor
public class HomeController {

    private final MemberService memberService;
    private final ItemService itemService;


    @RequestMapping
    public String hello(HttpServletRequest request , Model model){
        // 세션객체가 있을때는 기본페이지 localhost:8080/ 에 들어와도
        // 다시 회원폼 즉, localhost:8080/home 으로 이동하도록 설정

        HttpSession session = request.getSession(false);

        if(session == null){
            return "home";
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        // 세션에 회원 데이터가 없으면 로그인홈으로 이동하도록 설정
        if(loginMember == null){
            return "home";
        }

        model.addAttribute("member" , loginMember);

        return "home2";
    }

    //@RequestMapping("/home")
    public String home1(HttpServletRequest request , Model model){

        HttpSession session = request.getSession(false);
        Member attribute = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        model.addAttribute("member" , attribute);
        return "home2";
    }

    /**
     * session을 불러들일때 계속 HttpServletRequest , request.getSession , session.setAttribte
     * 너무 하는게 많고 귀찮다
     * 이떄 사용하는 것이 @SessionAttribute 어노테이션이다.
     */

    @RequestMapping("/home")
    public String home2(@SessionAttribute(name = SessionConst.LOGIN_MEMBER , required = false) Member loginMember, Model model){

        /*
        HttpSession session = request.getSession(false);
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        */

        // @SessionAttribute를 사용하면 이미 값이 있는 세션을 찾아서 세션의 값을 가져온다.
        // 이렇게 하면 위의 HttpServletRequest로 부터 request.getSession(false) 로 세션을 가져와서
        // session.getAttribute(SessionConst.LOGIN_MEMBER) 로 로그인 되었는지 찾는 코드를 작성할 필요없다

        // 다만 @SessionAttribute는 기존의 세션을 가져오는 것이기 떄문에 로그인을 할때처럼 세션에 값을 저장하는 경우에는 사용할 수 없다.

        model.addAttribute("member" , loginMember);

        if(loginMember == null){
            return "home";
        }

        return "home2";
    }


    @PostConstruct
    void init(){

        Member userA = new Member("userA", "A","1234" ,new Address("street1", "city1"), "010-4652-6327", Grade.FIRST, "zecrar@naver.com" , 0);
        Member userB = new Member("userB","B","1234" ,new Address("street2", "city3"), "010-4652-1234", Grade.THIRD, "zecrar@gmail.com" , 0);

        memberService.join(userA);
        memberService.join(userB);

        Item itemA = new Item("itemA", 10000, 50, ItemType.CHICKEN, true);
        Item itemB = new Item("itemB", 20000, 25, ItemType.PIZZA, true);

        itemService.ItemSave(itemA);
        itemService.ItemSave(itemB);
    }



}
