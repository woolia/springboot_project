package project.pr.controller.form;

import lombok.*;
import project.pr.domain.Item;
import project.pr.domain.Member;
import java.util.List;

@Data
@Getter @Setter
@NoArgsConstructor
public class OrderForm {

    private List<Item> items;
    private List<Member> members;
    private List<Object> counts;

    public OrderForm(List<Item> items) {
        this.items = items;
    }

    public OrderForm(List<Item> items , List<Member> members , List<Object> counts) {
        this.items = items;
        this.members = members;
        this.counts = counts;
    }


}
