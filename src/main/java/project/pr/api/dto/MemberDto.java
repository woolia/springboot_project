package project.pr.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.pr.domain.Member;
import project.pr.domain.status.Grade;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {

    private Long id;
    private String name;
    private String email;
    private int orderCount;
    private Grade grade;

    public MemberDto addMember(Member member){
        this.id = member.getId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.orderCount = member.getOrderCount();
        this.grade = member.getGrade();
        return this;
    }

}
