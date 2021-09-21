package project.pr.repository.db.custom;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.pr.domain.Address;
import project.pr.domain.Item;
import project.pr.domain.Member;
import project.pr.domain.Order;
import project.pr.domain.status.Grade;
import project.pr.domain.status.ItemType;
import project.pr.service.ItemService;
import project.pr.service.MemberService;
import project.pr.service.OrderService;

import java.util.List;


@SpringBootTest
@Transactional
class OrderRepositoryImplTest {

    @Autowired
    MemberService memberService;
    @Autowired
    OrderService orderService;
    @Autowired
    ItemService itemService;


    @Test
    void findSearchOrderMember() {

        Member member1 = new Member("userA","A","1234", new Address("street1", "city1"), "010-4652-3212", Grade.FIRST, "zecrar@naver.com");
        Member member2 = new Member("userB", "A","1234", new Address("street1", "city1"), "010-4652-3212", Grade.FIRST, "zecrar22@naver.com");

        memberService.join(member1);

        Item itemA = new Item("itemA", 10000, 10, ItemType.HAMBURGER, true);
        itemService.ItemSave(itemA);

        Long orderId = orderService.createOrder(member1.getId(), itemA.getId(), 5 , "FAST");

        List<Order> searchOrder = orderService.findSearchOrder(member1.getName(), orderService.findOne(orderId).getStatus());

        Order order = searchOrder.stream().findAny().orElse(null);

        Assertions.assertThat(order.getMember().getName()).isEqualTo("userA");


    }
}