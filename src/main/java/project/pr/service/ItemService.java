package project.pr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.pr.controller.form.ItemEnrollForm;
import project.pr.domain.Item;
import project.pr.repository.db.ItemRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    @Autowired
    private final ItemRepository itemRepository;

    @Transactional
    // commit 할때는 반드시 @Transactional 을 해야한다.
    public Long ItemSave(Item item){

        itemRepository.save(item);
        return item.getId();
    }

    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long id){
        Item item = itemRepository.findById(id).orElse(null);
        return item;
    }

    @Transactional
    public void updateItem(Long itemId, ItemEnrollForm form) {

        Item item = itemRepository.findById(itemId).orElse(null);
        item.update(form.getName() , form.getPrice() , form.getStockQuantity() , form.getItemType() ,form.getOpen());
    }

    public List<Item> findOpenItem() {
        List<Item> openItem = itemRepository.findOpenItem();
        return openItem;
    }
}
