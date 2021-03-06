package project.pr.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import project.pr.domain.Address;
import project.pr.domain.Item;
import project.pr.domain.Member;
import project.pr.domain.Order;
import project.pr.domain.exception.NotEnoughException;
import project.pr.domain.status.Grade;
import project.pr.repository.db.ItemRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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


    // ?????? ?????? ?????????
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


    // ?????? ?????? ?????? ?????????
    @Test
    void OverDiscount(){
        assertThrows(IllegalStateException.class , () ->{

            Member member = new Member("userA","A","1234", new Address("A", "Daejeon"), "010-4652-6327", Grade.FIRST, "zecrar@naver.com");
            memberService.join(member);

            Item itemA = new Item("itemA", 2000, 20);
            itemRepository.save(itemA);

            Long orderId = orderService.createOrder(member.getId(), itemA.getId(), 3 ,"FAST");
            // ?????? ????????? 6000??? ??????. ????????? ????????? FISRT ?????? 4000??? ??????????????? ?????? 2000?????? ??????.
            // ?????? ?????? ????????? 5000??? ?????? ?????? ????????? ????????? ???????????????.
        });
    }


}