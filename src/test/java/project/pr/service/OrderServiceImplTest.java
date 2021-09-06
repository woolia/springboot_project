package project.pr.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import project.pr.domain.*;
import project.pr.domain.exception.NotEnoughException;
import project.pr.domain.status.Grade;
import project.pr.repository.db.ItemRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceImplTest {

    @Autowired
    MemberService memberService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    OrderService orderService;

    @PersistenceContext
    EntityManager em;

    @Test
    @Rollback(value = false)
    @Transactional
    void createOrder() {

        Member member = new Member("userA","A","1234", new Address("A", "Daejeon"), "010-4652-6327", Grade.FIRST, "zecrar@anver.com");
        memberService.join(member);

        Item itemA = new Item("itemA", 10000, 20);
        itemRepository.save(itemA);

        Member findMember = memberService.findOne(member.getId());
        Item findItem = itemRepository.findById(itemA.getId()).orElse(null);

        Long orderId = orderService.createOrder(findMember.getId(), findItem.getId(), 5 , "FAST");

        Order order = orderService.findOne(orderId);

        System.out.println("findItem.getPrice() = " + findItem.getStockQuantity());
        System.out.println("order.getDiscountPrice() = " + order.getDiscountPrice());

        assertThat(findMember.getGrade()).isEqualTo(Grade.FIRST);
        assertThat(itemA.getStockQuantity()).isEqualTo(20 - 5);
        assertThat(order.getOrderItems().get(0).getOrderPrice()).isEqualTo(50000);
        assertThat(order.getDiscountPrice()).isEqualTo(46000);
    }


    @Test
    @Rollback(value = false)
    void cancelOrder() {

        Member member = new Member("userA","A","1234", new Address("A", "Daejeon"), "010-4652-6327", Grade.FIRST, "zecrar@naver.com");
        memberService.join(member);

        Item itemA = new Item("itemA", 10000, 20);
        itemRepository.save(itemA);

        Long orderId = orderService.createOrder(member.getId(), itemA.getId(), 5 , "FAST");

        em.flush();
        em.clear();

        orderService.cancelOrder(orderId);

        Item item = itemRepository.findById(itemA.getId()).orElse(null);

        assertThat(item.getStockQuantity()).isEqualTo(20);
    }


    // 수량 오버 테스트
    @Test
    void OverStock(){
        assertThrows(NotEnoughException.class , () ->{

            Member member = new Member("userA","A","1234", new Address("A", "Daejeon"), "010-4652-6327", Grade.FIRST, "zecrar@naver.com");
            memberService.join(member);

            Item itemA = new Item("itemA", 10000, 20);
            itemRepository.save(itemA);

            Long orderId = orderService.createOrder(member.getId(), itemA.getId(), 25 , "FAST");
        });
    }


    // 할인 상한 가격 테스트
    @Test
    void OverDiscount(){
        assertThrows(IllegalStateException.class , () ->{

            Member member = new Member("userA","A","1234", new Address("A", "Daejeon"), "010-4652-6327", Grade.FIRST, "zecrar@naver.com");
            memberService.join(member);

            Item itemA = new Item("itemA", 2000, 20);
            itemRepository.save(itemA);

            Long orderId = orderService.createOrder(member.getId(), itemA.getId(), 3 ,"FAST");
            // 전체 가격이 6000원 이다. 하지만 등급이 FISRT 라서 4000원 고정할인이 되어 2000원이 된다.
            // 할인 상한 가격이 5000원 이하 이기 때문에 오류를 발생시킨다.
        });
    }


}