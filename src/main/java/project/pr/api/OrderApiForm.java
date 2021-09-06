package project.pr.api;


import lombok.*;

@Data
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderApiForm {

    private Long memberId;
    private Long itemId;
    private int count;
    private String parcel;

}
