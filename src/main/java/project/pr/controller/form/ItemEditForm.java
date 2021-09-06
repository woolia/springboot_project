package project.pr.controller.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import project.pr.domain.status.ItemType;

@Data
@AllArgsConstructor
public class ItemEditForm extends ItemEnrollForm{

    private Long id;
    private String name;
    private Integer price;
    private Integer stockQuantity;

    private Boolean open; //판매 여부

    private ItemType itemType; // 상품 종류

}
