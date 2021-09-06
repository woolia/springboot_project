package project.pr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.pr.domain.*;
import project.pr.domain.status.DeliveryStatus;
import project.pr.domain.status.OrderStatus;
import project.pr.repository.db.ItemRepository;
import project.pr.repository.db.MemberRepository;
import project.pr.repository.db.OrderItemRepository;
import project.pr.repository.db.OrderRepository;
import project.pr.service.discountpolicy.DiscountPolicy;

import java.util.*;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService{


    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final OrderItemRepository orderItemRepository;
    private final DiscountPolicy discountPolicy;


    private static LinkedHashMap<String , Object> orders;


    @Override
    @Transactional
    public Long createOrder(Long memberId, Long itemId ,int count , String parcel) {

        Member findMember = memberRepository.findById(memberId).orElse(null);
        Item item = itemRepository.findById(itemId).orElse(null);


        // 먼저 멤버와 아이템을 불러옴 ( 멤버는 등급이 어떤지 , 아이템은 수량이 맞는지 또는 개당 아이템가격을 구하기 위해
        log.info("findMember = {}" ,findMember.getName());

        // 엔티티 조회

        Delivery delivery = new Delivery(findMember.getAddress(), DeliveryStatus.READY);
        // 배송 준비

        OrderItem orderItem = OrderItem.createOrderItem(item, count);
        orderItemRepository.save(orderItem);
        // 만들어진 orderItem 을 저장한다.


        log.info("orderItem = {}",orderItem.getCount());

        // 가져온 아이템을 넣고 , 파라미터로 받아온 몇개 아이템을 주문할지 정하는 count를 넣는다
        // 그러면 createOrderItem 에서 item 정보를 넣고 item의 개당 가격 * 주문 count 를 통해
        // 아이템 총 구매액이 나온다 ( 즉, 어떤 아이템A를 3개의 가격을 createOrderItem에서 item객체와 count를 넣으면
        // orderItem.OrderPrice = item.getPrice * count 가 된다.
        log.info("orderItem.getOrderPrice = {}" , orderItem.getOrderPrice());

        int discountedPrice = discountPolicy.discount(findMember, orderItem.getOrderPrice());
        // discountPolicy에서 discount 정책에 맞는 할인금액을 계산해준다.
        // 현재는 FixDiscount이지만 나중에는 Rate 등을 @OrderApp 의 @Configuration , @Bean 에노테이션을 통해 정할 수 있음
        // 가능한 이유는 다형성을 보장해서 코드를 작성했기 때문에 , 즉 , DiscountPolicy 인터페이스에 맞게 각각 FixDiscountPolicy, RateDiscountPolicy 등을 생성
        log.info("discountPrice = {}" , discountedPrice);

        Order order = Order.createOrder(findMember, discountedPrice, delivery, orderItem);
        order.addParcelType(parcel);
        // 마지막으로 멤버와 할인가격 , 배송지 정보 , 주문한 아이템을 바탕으로 주문객체를 생성한다.

        log.info("order.discountPrice = {}" , order.getDiscountPrice());

        orderRepository.save(order);


        return order.getId();
    }

    @Override
    @Transactional
    public List<Long> createOrder2(Long memberId, List<Long> itemIds, int count, String parcel) {

        Member findMember = memberRepository.findById(memberId).orElse(null);
        List<Item> items = new ArrayList<>();
        for (Long itemId : itemIds) {
            Item item = itemRepository.findById(itemId).orElse(null);
            items.add(item);
        }

        // 엔티티 조회

        Delivery delivery = new Delivery(findMember.getAddress(), DeliveryStatus.READY);
        // 배송 준비

        List<Long> orderIds = new ArrayList<>();

        for (Item item : items) {
            OrderItem orderItem = OrderItem.createOrderItem(item, count);
            orderItemRepository.save(orderItem);
            int discountedPrice = discountPolicy.discount(findMember, orderItem.getOrderPrice());
            Order order = Order.createOrder(findMember, discountedPrice, delivery, orderItem);
            order.addParcelType(parcel);
            orderRepository.save(order);

            orderIds.add(order.getId());
        }

        return orderIds;
    }

    @Override
    @Transactional
    public void createOrder3(Long memberId, Map<Long, Integer> countList , List<Long> itemIds, String parcel) {

        Member findMember = memberRepository.findById(memberId).orElse(null);
        Delivery delivery = new Delivery(findMember.getAddress(), DeliveryStatus.READY);

        for (Long itemId : countList.keySet()) {
            Item item = itemRepository.findById(itemId).orElse(null);
            Integer count = countList.get(itemId);
            OrderItem orderItem = OrderItem.createOrderItem(item, count);

            orderItemRepository.save(orderItem);
            int discountedPrice = discountPolicy.discount(findMember, orderItem.getOrderPrice());
            Order order = Order.createOrder(findMember, discountedPrice, delivery, orderItem);
            order.addParcelType(parcel);
            orderRepository.save(order);
        }

    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        log.info("+++++++++++++++++++++++");

        Order order = orderRepository.findById(orderId).orElse(null);
        log.info("order = {}" ,order);
        if(order != null){
            order.Cancel();
        }
    }

    public List<Order> findAll(){
        return orderRepository.findAll();
    }

    public Order findOne(Long id){
        Order order = orderRepository.findById(id).orElse(null);
        return order;
    }


    public List<Order> findSearchOrder(String name , OrderStatus orderStatus){
        return orderRepository.findSearchOrderMember(name , orderStatus);
    }

    @Override
    public void save(Long memberId, Long itemId ,int count) {
        Member member = memberRepository.findById(memberId).orElse(null);
        Item item = itemRepository.findById(itemId).orElse(null);
        Delivery delivery = new Delivery(member.getAddress(), DeliveryStatus.READY);
        OrderItem orderItem = OrderItem.createOrderItem(item, count);


    }

    @Override
    public List<Order> findMemberAndOrderItem(Long orderId) {
        List<Order> allQuaryDsl = orderRepository.findAllQuaryDsl(orderId);
        return allQuaryDsl;
    }



}
