package project.pr.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import project.pr.domain.Order;
import project.pr.repository.db.custom.OrderRepositoryCustom;

public interface OrderRepository extends JpaRepository<Order , Long> , OrderRepositoryCustom {

}
