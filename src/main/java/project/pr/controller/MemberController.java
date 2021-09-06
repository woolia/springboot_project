package project.pr.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project.pr.controller.form.LoginForm;
import project.pr.controller.form.MemberEnrollForm;
import project.pr.domain.Address;
import project.pr.domain.status.Grade;
import project.pr.domain.Member;
import project.pr.service.MemberService;
import project.pr.validator.MemberValidator;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {


    private final MemberService memberService;
    private final MemberValidator memberValidator;

    @GetMapping("/enroll")
    public String memberEnrollForm(Model model){
        log.info("======================");

        model.addAttribute("memberForm" , new MemberEnrollForm());
        // enroll.html 에 memberForm 속성이 있기 때문에 빈모델이라도 memberForm 을 보내야한다.
        return "/members/enroll";
    }


    @PostMapping("/enroll")
    public String memberEnroll(@ModelAttribute("memberForm") MemberEnrollForm memberForm , BindingResult bindingResult,
                               RedirectAttributes redirectAttributes){

        if(memberValidator.supports(memberForm.getClass())){
            memberValidator.validate(memberForm , bindingResult);
        }

        if(bindingResult.hasErrors()){
            log.info("errors = {}" , bindingResult);
            return "members/enroll";
        }

        // BindingResult 는 @ModelAttribute 바로 다음에 위치해야 한다고 한다.
        /*
        그래서 위에 파람을 @ModelAttribute("memberForm") MemberEnrollForm form , BindingResult bindingResult,
                               RedirectAttributes redirectAttributes 이렇게 설정한다.
         */


        log.info("{}" , memberForm.toString());
        Address address = new Address(memberForm.getStreet(), memberForm.getCity());

        Member newMember = new Member(memberForm.getName(), memberForm.getLoginId() ,memberForm.getPassword() , address, memberForm.getPhoneNumber(), Grade.NORMAL, memberForm.getEmail());
        memberService.join(newMember);

        // 중복된 이메일이 3개 이상일때 예외가 발생하므로 예외처리해야한다.
        
        redirectAttributes.addAttribute("name" , newMember.getName()+" 회원");
        redirectAttributes.addAttribute("status" , true);
        // 이렇게 redirectAttributes 를 사용해서 redirect가 요청했을때의 값을 view에서 설정할 수 있다.
        // 해당 속성은 ${param.name} , ${param.status} 이렇게 설정할 수 있다.
        // status 는 boolean 타입이다. 스프링에서 파라미터를 많이 사용해서 이렇게 사용하게 해준다고 한다.

        log.info("{}" ,newMember.toString());

        // 회원가입이 끝나면 로그인 폼으로 이동
        return "redirect:/";
    }


    @GetMapping("/list")
    public String memberList(Model model){

        log.info("======================");

        List<Member> members = memberService.findMembers();
        model.addAttribute("members" , members);

        return "/members/memberList";
    }


}
