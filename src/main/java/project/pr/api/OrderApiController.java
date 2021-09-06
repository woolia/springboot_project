package project.pr.api;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import project.pr.api.dto.OrderApiDto;
import project.pr.domain.Item;
import project.pr.domain.Member;
import project.pr.domain.Order;
import project.pr.service.ItemService;
import project.pr.service.MemberService;
import project.pr.service.OrderService;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderApiController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @PostMapping("/all")
    public ResultList<Object> MemberAndItem(){
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        return new ResultList(members , items);
    }

    @PostMapping("/do")
    public OrderApiDto AllorderApi(@RequestBody OrderApiForm orderApiForm){
        Long orderId = orderService.createOrder(orderApiForm.getMemberId(), orderApiForm.getItemId(), orderApiForm.getCount(),"빠른배송");

        log.info("{} , {} ,{} ",orderApiForm.getMemberId(), orderApiForm.getItemId(), orderApiForm.getCount() );
        Order one = orderService.findOne(orderId);
        log.info("{}" , one.getMember().getName());
        return new OrderApiDto(one , one.getMember());
    }


    @PostMapping("/findOrderList")
    public OrderList<OrderApiDto> AllorderApi2(@RequestBody HashMap<String , Long> Param){

        List<Order> orders = orderService.findMemberAndOrderItem(Param.get("orderId"));

        List<OrderApiDto> collect = orders.stream()
                .map(o -> new OrderApiDto(o, o.getMember()))
                .collect(Collectors.toList());
        return new OrderList(collect);
    }


    @AllArgsConstructor
    @Data
    class ResultList<T>{
        private T Members;
        private T Items;
    }

    @AllArgsConstructor
    @Data
    class OrderList<T>{
        private T Orders;
    }

}
