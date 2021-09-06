package project.pr.service;

import project.pr.domain.Order;
import project.pr.domain.status.OrderStatus;

import java.util.List;
import java.util.Map;

public interface OrderService {

    Long createOrder(Long memberId , Long itemId ,int count , String parcel);
    // 멤버의 id , 아이템의 id , 몇개의 아이템을 살건지

    List<Long> createOrder2(Long memberId , List<Long> itemIds ,int count , String parcel);

    void createOrder3(Long memberId , Map<Long,Integer> countList ,List<Long> itemIds, String parcel);

    void cancelOrder(Long orderId);

    public List<Order> findAll();
    public Order findOne(Long id);

    public List<Order> findSearchOrder(String name , OrderStatus orderStatus);

    void save(Long memberId, Long itemId , int count);

    List<Order> findMemberAndOrderItem(Long orderId);

}
