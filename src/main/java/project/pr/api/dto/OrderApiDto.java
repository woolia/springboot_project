package project.pr.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import project.pr.domain.Member;
import project.pr.domain.Order;
import project.pr.domain.OrderItem;
import project.pr.domain.status.Grade;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class OrderApiDto {

    private LocalDateTime orderDate;
    private int discountPrice;
    private String parcelType;

    private String memberName;
    private String email;
    private int orderCount;
    private Grade grade;

    private List<String> itemName = new ArrayList<>();
    private List<Integer> itemPrice = new ArrayList<>();
    private List<Boolean> open = new ArrayList<>();
    private List<Integer> stockQuantity = new ArrayList<>();


    public OrderApiDto(Order order , Member member) {
        orderDate = order.getOrderDate();
        discountPrice = order.getDiscountPrice();
        parcelType = order.getParcelType();

        memberName = member.getName();
        email = member.getEmail();
        orderCount = member.getOrderCount();
        grade = member.getGrade();

        log.info("{}",order.getOrderItems().size());
        for (OrderItem orderItem : order.getOrderItems()) {
            itemName.add(orderItem.getItem().getName());
            itemPrice.add(orderItem.getItem().getPrice());
            open.add(orderItem.getItem().getOpen());
            stockQuantity.add(orderItem.getItem().getStockQuantity());
        }
    }
}
