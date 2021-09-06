package project.pr.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project.pr.controller.form.ItemEditForm;
import project.pr.controller.form.ItemEnrollForm;
import project.pr.domain.Item;
import project.pr.domain.status.ItemType;
import project.pr.service.ItemService;
import project.pr.validator.ItemValidator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Controller
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final ItemValidator itemValidator;

    // @Autowired 를 해주면 자동으로 new 객체를 생성해준다. 그래서 아래와 같이 작성하면 객체를 생성하는 것과 똑같다.
    /*
    @Autowired
    private ItemValidator itemValidator;
    -------------------------------------
    private ItemValidator itemValidator = new ItemValidator();

    이 두가지 모두 같은 것
    그런데 생성자로 주입시키면
    @Autowired 하나가 자동으로 들어간다

    public ItemController(){
        ItemService itemService = new ItemService();
        ItemValidator itemValidator = new ItemValidator();
    }
    그런데 생성자가 하나더 생긴다면
    그때는 @Autowired를 붙여줘야 생성자 주입이 된다.

    그래서 이런 것을 예방하고자 @RequiredArgsConstructor 을 넣고 필드객체에 final 을 붙여주는 것
     */


    // @InitBinder는 이 컨트롤러 클래스가 요청될때마다 dataBinder에 itemValidator를 넣어놓기 때문에
    // ItemController 에서의 모든 Mapping 에서 검증기를 사용할 수 있다.

    @InitBinder
    public void init(WebDataBinder dataBinder){
        dataBinder.addValidators(itemValidator);
    }


    @ModelAttribute("itemType")
    public ItemType[] itemType(){
        ItemType[] values = ItemType.values();
        log.info(" itemType = {}",values);
        return values;
    }

    @GetMapping("/enroll")
    public String itemEnrollForm(Model model){
        ItemEnrollForm itemEnrollForm = new ItemEnrollForm();
        model.addAttribute("itemForm" , new ItemEnrollForm());
        return "/items/ItemEnroll";
    }

    //@PostMapping("/enroll")
    public String itemEnroll(@ModelAttribute("itemForm") ItemEnrollForm itemForm , BindingResult bindingResult ,
                             RedirectAttributes redirectAttributes){

        if(!StringUtils.hasText(itemForm.getName())){
            // bindingResult.addError(new FieldError("form" , "name" , form.getName() , false , new String[]{"required.item.itemName","required.default"} , null , null));
            // new String[] 에서 만약에 required.item.itemName 이라는 값이 없으면 required.default를 찾는다는 의미다.

            // 위의 addError 니 new FieldError니 넣는게 너무 많고 귀찮다 이것을 해결해 주는게 rejectValue와 reject
            // rejectValue는 new FieldError 를 대체해준다. 파라미터로는 field 이름과 properties의 맨앞코드만 넣으면 된다.
            // 그리고 rejectValue는 fieldError를 나타내고 reject는 ObjectError 즉,  globalError를 나타낸다.
            bindingResult.rejectValue("name" , "ItemRequired");
        }

        if(itemForm.getPrice() == null || itemForm.getPrice() <= 1000 || itemForm.getPrice() >= 100000){
            // bindingResult.addError(new FieldError("form" , "price" , form.getPrice() , false , new String[]{"range.item.price"} , new Object[]{1000,100000} , null));
            bindingResult.rejectValue("price" , "ItemRange",new Object[]{1000,100000},null);
        }

        if(itemForm.getStockQuantity() == null || itemForm.getStockQuantity() < 10){
            // bindingResult.addError(new FieldError("form" , "stockQuantity" , form.getStockQuantity() , false , new String[]{"max.item.quantity"} , new Object[]{10} , null));
            bindingResult.rejectValue("stockQuantity" , "ItemMin", new Object[]{10} , null);
        }

        // 특정 필드가 아닌 global 에러이기 때문에 new ObjectError 에 담는다.

        if(itemForm.getPrice() != null && itemForm.getStockQuantity() != null){
            int resultPrice = itemForm.getPrice() * itemForm.getStockQuantity();
            if(resultPrice < 100000){
                // bindingResult.addError(new ObjectError("form" , new String[]{"totalPriceMin"} , new Object[]{resultPrice} , null));
                bindingResult.reject("totalPriceMin",new Object[]{100000} , null);
            }
        }

        if(bindingResult.hasErrors()){
            log.info("errors = {}" , bindingResult);
            return "items/ItemEnroll";
        }


        Item item = new Item(itemForm.getName(), itemForm.getPrice(), itemForm.getStockQuantity() , itemForm.getItemType() , itemForm.getOpen());
        itemService.ItemSave(item);

        log.info("{}",item.getOpen());

        redirectAttributes.addAttribute("status" , true);
        redirectAttributes.addAttribute("name", item.getName()+" 상품");

        return "redirect:/home";
    }

    // 아래 메서드는 위의 메서드의 검증로직 즉, controller에서 담당하는 검증로직을 validator라는 검증 클래스로 따로 분리해서
    // controller가 맡은 역할을 줄여주는 메서드이다.
    // 검증로직을 전부 ItemValidator 클래스로 옮긴다.
    // @PostMapping("/enroll")
    public String itemEnroll2(@ModelAttribute("itemForm") ItemEnrollForm itemForm , BindingResult bindingResult ,
                             RedirectAttributes redirectAttributes){

        // validator 클래스를 가져오기 위해서는 의존성 bean 주입으로 넣는다.
        // 따라서 위에 private final ItmeValidator validator = new ItemValidator; 를 작성
        // 또한 스프링에 등록해야 하기 때문에 ItemValidator 클래스에 @Component 를 넣는다 그래야 스캔대상으로 빈이 등록되기 때문에

        // 싱글톤객체를 유지하므로 필드에서 객체를 선언하지말고 그냥 생성한다.
        // ItemValidator 객체는 ItemService객체와는 다르게 생성자가 존재하지 않아서
        // 직접 생성해줘야 한다.


        if(itemValidator.supports(itemForm.getClass())){
            itemValidator.validate(itemForm , bindingResult);
        }


        if(bindingResult.hasErrors()){
            log.info("errors = {}" , bindingResult);
            return "items/ItemEnroll";
        }


        Item item = new Item(itemForm.getName(), itemForm.getPrice(), itemForm.getStockQuantity() , itemForm.getItemType() , itemForm.getOpen());
        itemService.ItemSave(item);

        log.info("{}",item.getOpen());

        redirectAttributes.addAttribute("status" , true);
        redirectAttributes.addAttribute("name", item.getName()+" 상품");

        return "redirect:/home";
    }


    // @ModelAttribute 객체 앞에 @Validated 어노테이션을 넣으면 해당 form에 대해서 자동으로 검증을 해준다.
    // 그래서 해당 메서드에서 검증기를 불러오지 않아도 된다.
    @PostMapping("/enroll")
    public String itemEnroll3(@Validated @ModelAttribute("itemForm") ItemEnrollForm itemForm , BindingResult bindingResult ,
                              RedirectAttributes redirectAttributes){
        
//        if(itemValidator.supports(itemForm.getClass())){
//            itemValidator.validate(itemForm , bindingResult);
//        }
        
        // 위의 메서드가 필요 없음


        if(bindingResult.hasErrors()){
            log.info("errors = {}" , bindingResult);
            return "items/ItemEnroll";
        }


        Item item = new Item(itemForm.getName(), itemForm.getPrice(), itemForm.getStockQuantity() , itemForm.getItemType() , itemForm.getOpen());
        itemService.ItemSave(item);

        log.info("{}",item.getOpen());

        redirectAttributes.addAttribute("status" , true);
        redirectAttributes.addAttribute("name", item.getName()+" 상품");

        return "redirect:/home";
    }

    @GetMapping("/list")
    public String itemList(Model model){

        List<Item> items = itemService.findItems();

        model.addAttribute("items" , items);
        return "items/itemList";
    }

    @GetMapping("{itemId}/edit")
    public String editForm(@PathVariable("itemId") Long itemId , Model model){

        Item findItem = itemService.findOne(itemId);

        ItemEditForm EditItem = new ItemEditForm(findItem.getId(), findItem.getName(), findItem.getPrice(), findItem.getStockQuantity(), findItem.getOpen(), findItem.getItemType());

        model.addAttribute("form" , EditItem);
        return "items/updateItem";
    }

    @PostMapping("{itemId}/edit")
    public String editItem(@PathVariable("itemId") Long itemId , @Validated @ModelAttribute ItemEditForm form , BindingResult bindingResult , Model model){

        log.info("{}" , form.toString());
        itemService.updateItem(itemId, form);
        return "redirect:/items/list";
    }




}
