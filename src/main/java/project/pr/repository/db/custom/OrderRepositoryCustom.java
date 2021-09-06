package project.pr.repository.db.custom;

import project.pr.domain.Order;
import project.pr.domain.status.OrderStatus;

import java.util.List;

public interface OrderRepositoryCustom {

    public List<Order> findSearchOrderMember(String name , OrderStatus orderStatus);

    public List<Order> findAllQuaryDsl(Long orderId);

}
