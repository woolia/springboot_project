package project.pr.repository.db.custom;

import project.pr.domain.Item;

import java.util.List;

public interface ItemRepositoryCustom {

    List<Item> findOpenItem();

}
