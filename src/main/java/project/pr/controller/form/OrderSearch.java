package project.pr.controller.form;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import project.pr.domain.status.OrderStatus;

@Getter @Setter
public class OrderSearch {

    private String name;
    private OrderStatus orderStatus;
    private int quantity;


}
