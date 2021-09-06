package project.pr.repository.db.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import project.pr.domain.Item;
import project.pr.domain.QItem;

import javax.persistence.EntityManager;

import java.util.List;

import static project.pr.domain.QItem.*;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public ItemRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public List<Item> findOpenItem() {
        List<Item> fetch = queryFactory.select(item).from(item)
                .where(item.open.eq(true))
                .fetch();
        return fetch;
    }
}
