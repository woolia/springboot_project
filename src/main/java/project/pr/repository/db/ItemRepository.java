package project.pr.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import project.pr.domain.Item;
import project.pr.repository.db.custom.ItemRepositoryCustom;

public interface ItemRepository extends JpaRepository<Item , Long> , ItemRepositoryCustom {
}
