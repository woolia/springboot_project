package project.pr.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project.pr.controller.form.OrderForm;
import project.pr.controller.form.OrderSearch;
import project.pr.controller.form.ordersForm;
import project.pr.domain.*;
import project.pr.service.ItemService;
import project.pr.service.MemberService;
import project.pr.service.OrderService;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    private static LinkedHashMap<Long , Member> savemember = new LinkedHashMap<>();
    private static LinkedHashMap<Long , Item> saveitem = new LinkedHashMap<>();

    private void viewSaveMembers(Long id){
        savemember.put(id , memberService.findOne(id));
    }

    private void viewSaveItems(Long id){
        saveitem.put(id , itemService.findOne(id));
    }

    @ModelAttribute("parcelType")
    public LinkedHashMap<String , String> parcelType(){
        LinkedHashMap<String , String> parcel = new LinkedHashMap<>();
        parcel.put("FAST" , "빠른배송");
        parcel.put("NORMAL" , "일반배송");
        return parcel;
    }


    @GetMapping("/1")
    public String orderForm(Model model){

        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members" , members);
        model.addAttribute("items" , items);

        return "order/orderForm1";
    }

    @PostMapping("/1")
    public String order1(@RequestParam("memberId") Long memberId ,
                         @RequestParam("itemId") Long itemId,
                         @RequestParam("count") int count,
                         @RequestParam("parcelType") String parcel,
                         @ModelAttribute("order") OrderForm orderForm,
                         RedirectAttributes redirectAttributes){


        Long orderId = orderService.createOrder(memberId, itemId, count, parcel);

        Order findOrder = orderService.findOne(orderId);

        redirectAttributes.addAttribute("status" , true);
        redirectAttributes.addAttribute("name" , findOrder.getMember().getName()+" 회원 "+ findOrder.getOrderItems().get(0)+" 상품 주문");

        return "redirect:/home";
    }

    @GetMapping("/2")
    public String orderForm2(Model model){

        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members" , members);
        model.addAttribute("items" , items);
        model.addAttribute("order" , new OrderForm(items));

        return "order/orderForm2";
    }

    @PostMapping("/2")
    public String order2(@RequestParam("memberId") Long memberId ,
                        @RequestParam("count") int count,
                        @RequestParam("parcelType") String parcel,
                        @ModelAttribute("order") OrderForm orderForm,
                        RedirectAttributes redirectAttributes){


        List<Long> itemIds = orderForm.getItems().stream().map(item -> item.getId()).collect(Collectors.toList());

        orderService.createOrder2(memberId,itemIds, count , parcel);

        return "redirect:/home";
    }


    @GetMapping("/3")
    public String orderForm3(Model model){

        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

//        model.addAttribute("members" , members);
//        model.addAttribute("items" , items);
        model.addAttribute("order" , new OrderForm(items,members,new ArrayList<>()));

        return "order/orderForm3";
    }

    @PostMapping("/3")
    public String order3(@RequestParam("memberId") Long memberId ,
                         @RequestParam("parcelType") String parcel,
                         @ModelAttribute("order") OrderForm orderForm,
                         @RequestParam("counts") List<Integer> counts,
                         RedirectAttributes redirectAttributes){

        //List<Item> items = orderForm.getItems().stream().filter(item -> item.getOpen().equals(true)).collect(Collectors.toList());

        Map<Long, Integer> list = new LinkedHashMap<>();

        // 키값을 itemId 로 넣고 value를 count로 넣는다.
        // 그리고 order 할때 Map의 키값인 itemId 와 맴버 리포지토리에서 불러온 itemId를 비교해서 같은것 끼리만 주문로직을 실행하면 됨

        List<Long> itemIds = orderForm.getItems().stream().map(item -> item.getId()).collect(Collectors.toList());

        for (Long itemId : itemIds) {
            log.info("{}" , itemId);
        }


        int index = 0;

        for (Integer count : counts) {
            if(count != null){
                list.put(itemIds.get(index) , count);
                log.info("주문한 아이템의 아이디는 {} , 주문수량은 {}" , itemIds.get(index) , count);
                ++index;
            }

        }

        orderService.createOrder3(memberId,list,itemIds, parcel);
        return "redirect:/home";
    }





    @PostMapping("/save")
    public String save(@RequestParam("memberId") Long memberId ,
                       @RequestParam("items") List<Long> itemIds,
                       @RequestParam("count") int count,
                       @RequestParam("parcelType") String parcel,
                       RedirectAttributes redirectAttributes){

        log.info("===================================");

        ordersForm ordersForm = new ordersForm();

        log.info("{} . {} . {} . {}" , memberId , itemIds , count , parcel);

        ordersForm.setMemberName(memberService.findOne(memberId).getName());
        ordersForm.setCount(count);
        for (Long itemId : itemIds) {
            ordersForm.setItemName(itemService.findOne(itemId).getName());
        }

        redirectAttributes.addAttribute("status" , true);
        redirectAttributes.addAttribute("orderMember" , ordersForm.getMemberName());
        redirectAttributes.addAttribute("orderItem" , ordersForm.getItemName());
        redirectAttributes.addAttribute("orderCount" , ordersForm.getCount());
        return "redirect:/order/3";
    }

    @GetMapping("/list")
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch , Model model){

        List<Order> orders = orderService.findAll();
        model.addAttribute("orders" , orders);
        return "order/orderList";
    }





}
