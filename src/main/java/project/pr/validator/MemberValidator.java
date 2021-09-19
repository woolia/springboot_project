package project.pr.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import project.pr.controller.form.MemberEnrollForm;
import project.pr.service.MemberService;

@Component
@RequiredArgsConstructor
public class MemberValidator implements Validator {

    private final MemberService memberService;


    @Override
    public boolean supports(Class<?> clazz) {
        return MemberEnrollForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        MemberEnrollForm target1 = (MemberEnrollForm) target;

        // 검증
        // 회원가입시 이름이 없을때 String 은 StringUtils 의 hasText 사용 , 숫자타입은 값이 null 인지 판단
        if(!StringUtils.hasText(target1.getName())){
            // bindingResult.addError(new FieldError("form" , "name" ,form.getName() ,false, null,null, "이름을 입력하세요"));
            errors.rejectValue("name" , "MemberRequired");
        }

        if(!StringUtils.hasText(target1.getPassword())){
            // bindingResult.addError(new FieldError("form" , "name" ,form.getName() ,false, null,null, "이름을 입력하세요"));
            errors.rejectValue("password" , "MemberRequired");
        }

        if(! StringUtils.hasText(target1.getPhoneNumber())){
            //bindingResult.addError(new FieldError("form" , "phoneNumber" ,form.getPhoneNumber() , false , null,null ,"회원 가입시 전화번호는 필수 입니다."));
            errors.rejectValue("phoneNumber" , "MemberRequired");
        }

        // 특정 필드가 아닌 복합 룰 검증할때는 글로벌 오류로 처리 new ObjectError


        // 검증에 실패했을때 다시 입력 폼으로 보내면 된다.
        // 그리고 bindingresult 는 binding 시 view 에 model 과 같이 넘어간다고 한다.

        if(memberService.findLoginId(target1.getLoginId()) != null){
            errors.rejectValue("loginId","existId");
        }

        if(! StringUtils.hasText(target1.getLoginId())){
            errors.rejectValue("loginId","MemberRequired");
        }

        if (memberService.findEmail(target1.getEmail()) != null){
            errors.rejectValue("email" , "existEmail");
        }


    }
}
