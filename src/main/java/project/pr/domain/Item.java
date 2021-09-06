package project.pr.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.pr.domain.exception.NotEnoughException;
import project.pr.domain.status.ItemType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter(value = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
public class Item {

    @Column(name = "ITEM_ID")
    @Id @GeneratedValue
    private Long id;

    private String name;

    private int price;
    private int stockQuantity;

    private Boolean open; //판매 여부
    // Boolean 타입을 사용해서 아이템을 팔지 안팔지 여부를 결정할 수 있다.

    @Enumerated(value = EnumType.STRING)
    private ItemType itemType; // 상품 종류

    @OneToMany(mappedBy = "item")
    @Column(name = "ORDERITEMS")
    private List<OrderItem> orderItemList = new ArrayList<>();

    public Item(String name, int price, int stockQuantity) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public Item(String name, int price, int stockQuantity ,ItemType itemType , Boolean open) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.itemType = itemType;
        this.open = open;
    }

    /**
     *  재고 증가
     */
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    /**
     * 재고 감소
     */
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;

        if(restStock <0){
            throw new NotEnoughException("재고보다 요청 수량이 많습니다.");
        }

        this.stockQuantity -= quantity;
    }


    public void update(String name, int price, int stockQuantity, ItemType itemType, Boolean open) {
        setName(name);
        setPrice(price);
        setStockQuantity(stockQuantity);
        setItemType(itemType);
        setOpen(open);
    }
}
