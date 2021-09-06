package project.pr.repository.db.custom;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.util.StringUtils;
import project.pr.domain.Order;
import project.pr.domain.QItem;
import project.pr.domain.QOrderItem;
import project.pr.domain.status.OrderStatus;
import project.pr.repository.db.OrderRepository;

import javax.persistence.EntityManager;
import java.util.List;

import static project.pr.domain.QMember.member;
import static project.pr.domain.QOrder.order;

public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public OrderRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Order> findSearchOrderMember(String name , OrderStatus orderStatus) {

        List<Order> orders = queryFactory.select(order).from(order).join(order.member, member)
                .where(nameEq(name),
                        orderStatusEq(orderStatus))
                .fetch();
        return orders;
    }


    private BooleanExpression orderStatusEq(OrderStatus orderStatus) {
        BooleanExpression ex = orderStatus != null ? order.status.eq(orderStatus) : null;
        return ex;
    }

    private BooleanExpression nameEq(String name) {
        BooleanExpression ex = StringUtils.hasText(name) ? member.name.eq(name) : null;
        return ex;
    }


    public List<Order> findAllQuaryDsl(Long orderId){
        List<Order> fetch = queryFactory.select(order).from(order)
                .leftJoin(order.member, member)
                .fetchJoin()
                .leftJoin(order.orderItems, QOrderItem.orderItem)
                .fetchJoin()
                .leftJoin(QOrderItem.orderItem.item , QItem.item)
                .fetchJoin()
                .fetch();
        return fetch;
    }


}
