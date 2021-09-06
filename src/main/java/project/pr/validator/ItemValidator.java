package project.pr.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import project.pr.controller.form.ItemEnrollForm;
import project.pr.domain.Item;

@Slf4j
@Component
public class ItemValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        log.info("{}" , ItemEnrollForm.class.isAssignableFrom(clazz));
        return ItemEnrollForm.class.isAssignableFrom(clazz);
        // 파라미터로 넘어오는 clazz(객체의 클래스타입) 이 Item.class 와 같은지 판단
        // 만약 true(같을때) 가 나오면 아래의 validate 메서드가 실행하도록 하고 false(다르다면) 가 나오면 validate 메서드가 실행되지 않도록
        // if 문으로 작성하면 된다.
        // isAssignableFrom() 을 사용하면 자식객체(ex Item의 자식객체 Book) 또한 지원할 수 있다.

        // 파라미터로 넘어오는 clazz가 @ModelAttribute에 담긴 Form (여기서는 ItemEnrollForm 이 들어온다.)
    }

    @Override
    public void validate(Object target, Errors errors) {
        // target은 객체 즉 , 여기서는 Item 이 된다.
        // errors 는 BindingResult가 넘어온다. 왜냐하면 Errors의 자식객체가 BindingResult 이기 때문에

        ItemEnrollForm item = (ItemEnrollForm) target;

        if(!StringUtils.hasText(item.getName())){
            // bindingResult.addError(new FieldError("form" , "name" , form.getName() , false , new String[]{"required.item.itemName","required.default"} , null , null));
            errors.rejectValue("name" , "ItemRequired");
            // errors 에서도 rejectValue가 존재한다.
        }

        if(item.getPrice() == null || item.getPrice() <= 1000 || item.getPrice() >= 100000){
            // bindingResult.addError(new FieldError("form" , "price" , form.getPrice() , false , new String[]{"range.item.price"} , new Object[]{1000,100000} , null));
            errors.rejectValue("price" , "ItemRange",new Object[]{1000,100000},null);
        }

        if(item.getStockQuantity() == null || item.getStockQuantity() < 10){
            // bindingResult.addError(new FieldError("form" , "stockQuantity" , form.getStockQuantity() , false , new String[]{"max.item.quantity"} , new Object[]{10} , null));
            errors.rejectValue("stockQuantity" , "ItemMin", new Object[]{10} , null);
        }

        if(item.getPrice() != null && item.getStockQuantity() != null){
            int resultPrice = item.getPrice() * item.getStockQuantity();
            if(resultPrice < 100000){
                // bindingResult.addError(new ObjectError("form" , new String[]{"totalPriceMin"} , new Object[]{resultPrice} , null));
                errors.reject("totalPriceMin",new Object[]{100000} , null);
            }
        }

    }
}
