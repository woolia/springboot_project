package project.pr.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter(value = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "ORDER_ITEM_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY ,cascade = CascadeType.ALL)
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    @JoinColumn(name = "ITEM_ID")
    private Item item;

    private int count;

    private int orderPrice;

    public void setOrder(Order order) {
        if (order != null){
            this.order = order;
            order.getOrderItems().add(this);
        }
    }

    private void setItem(Item item) {
        if(item != null){
            this.item = item;
            item.getOrderItemList().add(this);
        }
    }

    public static OrderItem createOrderItem(Item item, int count) {

        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setCount(count);
        orderItem.setOrderPrice(item.getPrice() * count);

        item.removeStock(count);
        // 주문을 하면 재고가 감소해야 한다.

        return orderItem;
    }

    public void cancel(){
        int count = getCount();
        getItem().addStock(count);
        // getItem() 으로 가져와서 addStock 해줘서 재고를 다시 채워줘야 한다.
    }

}
