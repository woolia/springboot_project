package project.pr.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import project.pr.domain.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem , Long> {
}
