package project.pr.controller.form;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import project.pr.domain.Member;

@Data
@ToString
public class MemberEnrollForm {

    private String name;

    private String loginId;
    private String password;

    private String city;
    private String street;

    private String phoneNumber;
    private String email;

}
