package project.pr.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.pr.domain.status.DeliveryStatus;
import project.pr.domain.status.OrderStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ORDER_TABLE")
public class Order {
    // ORDER 라는 이름의 테이블은 사용을 하지 못한다고 한다.
    // DB SQL 에서 ORDER 라는 키워드를 사용하기 때문에 사용하지 못한다고 한다.
    // 그래서 테이블명을 ORDER_TABLE 로 설정하였다.

    @Id @GeneratedValue
    @Column(name = "ORDER_ID")
    private Long id;

    private LocalDateTime orderDate;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;

    private int discountPrice;

    // 택배 타입 FAST , NORMAL
    private String parcelType;


    @ManyToOne(fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    private Member member;
    // @JoinColumn 할때는 컬럼의 이름 넣어야한다.
    // Member_ID 컬럼과 조인을 해야 MEMBER_ID 를 통해서 ORDER 테이블과 MEMBER 테이블 둘다 조회할 수 있기 때문에
    // @JOinColumn 에는 @Id 값을 넣어야 한다고 생각한다.

    @OneToMany(mappedBy = "order" , cascade =  CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY ,cascade = CascadeType.ALL)
    private Delivery delivery;


    private int totalOrderCount;



    private void setOrderItemList(OrderItem orderItem) {
        orderItem.setOrder(this);
        orderItems.add(orderItem);
    }

    public static Order createOrder(Member member, int discountPrice , Delivery delivery, OrderItem... orderItems) {
        // ... 문법은 여러개의 orderItems 를 받을 수 있다는 의미 1개를 받아도 되고 여러개 collection 으로 받아도 상관없음
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.ORDER);
        // OrderDate 와 OrderStatus 는 주문하는 즉시 주문한 시간 , 주문상태등을 자동 설정한다.

        order.setMember(member);
        order.addMemberOrderCount2(member);
        // 이 두개의 로직을 하나로 묶을 수 있을 거 같다.

        order.setDiscountPrice(discountPrice);
        for (OrderItem orderItem : orderItems) {
            order.setOrderItemList(orderItem);
        }
        order.setDelivery(delivery);
        return order;
    }

    // 비즈니스 로직

    public void Cancel(){
        if(delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송된 상품입니다.");
        }

        status = OrderStatus.CANCEL;
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    // 조회 로직
    // 전체 주문 가격 (ex 아이템1 3개 + 아이템2 2개 + 아이템3 1개 의 가격
    public int TotalPrice(){
        int totalPrice = 0;

        for (OrderItem orderItem : orderItems) {
            int orderItemPrice = orderItem.getOrderPrice();
            totalPrice += orderItemPrice;
        }
        return totalPrice;
    }

    // 배송 정보 더하기
    public void addParcelType(String parcel){
        this.parcelType = parcel;
    }

    // 맴버의 orderCount수 늘리기
    // orderCount를 늘려서 늘린만큼 회원등급을 상승
    public void addMemberOrderCount(Member member){
        member.upgradeGrade();
    }


    public void addMemberOrderCount2(Member member){
        totalOrderCount++;
        member.upgradeGrade2(this.totalOrderCount);
    }

}
