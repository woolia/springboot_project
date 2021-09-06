package project.pr.controller.form;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.pr.domain.Item;

import java.util.List;

@Data
@Getter @Setter
@NoArgsConstructor
public class ordersForm {

    private List<String> memberName;
    private List<Integer> count;
    private List<String> itemName;

    public List<String> getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName.add(memberName);
    }

    public List<Integer> getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count.add((Integer) count);
    }

    public List<String> getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName.add(itemName);
    }
}
