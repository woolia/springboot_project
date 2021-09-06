package project.pr.controller.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import project.pr.domain.status.ItemType;

@Data
@Slf4j
@ToString
@NoArgsConstructor
public class ItemEnrollForm {

    private String name;
    private Integer price;
    private Integer stockQuantity;

    private Boolean open; //판매 여부

    private ItemType itemType; // 상품 종류

    public ItemEnrollForm(String name, Integer price, Integer stockQuantity) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public void addOpenAndItemType(Boolean open , ItemType itemType){
        this.open = open;
        this.itemType = itemType;
    }


}
