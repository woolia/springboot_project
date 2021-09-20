package project.pr.domain;

import lombok.*;
import project.pr.domain.status.Grade;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter @Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member implements Serializable {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    private String name;

    private String loginId;
    private String password;

    @Embedded
    private Address address;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    private String email;
    private int orderCount;

    private int cash; // 잔액

    public Member(String name, String loginId, String password ,  Address address, String phoneNumber , Grade grade , String email) {
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.grade = grade;
        this.email = email;
    }

    public Member(String name, String loginId, String password ,  Address address, String phoneNumber , Grade grade , String email , int cash, Role role) {
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.grade = grade;
        this.email = email;
        this.cash = cash;
        this.role = Role.USER;
    }

    public void change(String name, Address address, String phoneNumber , Grade grade , String email){
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.grade = grade;
        this.email = email;
    }
    public Member changeinfo(Long id , String name , String email , int orderCount ){
        this.id = id;
        this.name = name;
        this.email = email;
        this.orderCount = orderCount;
        upgradeGradeApi();
        return this;
    }

    public void updateGrade(Grade grade){
        this.grade = grade;
    }

    public Member otherLoginMemberUpdate(String name , String email){
        this.name = name;
        this.loginId =null;
        this.password = null;
        this.address = null;
        this.phoneNumber = null;
        this.grade = Grade.NORMAL;
        this.email = email;
        this.cash = 0;
        this.role = Role.USER;
        return this;
    }

    public Member (String name , String email){
        this.name = name;
        this.loginId =null;
        this.password = null;
        this.address = null;
        this.phoneNumber = null;
        this.grade = Grade.NORMAL;
        this.email = email;
        this.cash = 0;
        this.role = Role.USER;
    }

    public void upgradeGrade(){
        ++orderCount;
        switch (orderCount/2){
            case 0: this.grade=Grade.NORMAL; break;
            case 1: this.grade=Grade.FOURTH; break;
            case 2 : this.grade=Grade.THIRD;break;
            case 3: this.grade=Grade.SECOND;break;
            case 4: this.grade=Grade.FIRST;break;
        }
    }

    public void upgradeGradeApi(){
        switch (orderCount/2){
            case 0: this.grade=Grade.NORMAL; break;
            case 1: this.grade=Grade.FOURTH; break;
            case 2 : this.grade=Grade.THIRD;break;
            case 3: this.grade=Grade.SECOND;break;
            case 4: this.grade=Grade.FIRST;break;
        }
    }


    public void upgradeGrade2(int totalOrderCount) {
        orderCount = totalOrderCount;
        switch (totalOrderCount/2){
            case 0: this.grade=Grade.NORMAL; break;
            case 1: this.grade=Grade.FOURTH; break;
            case 2 : this.grade=Grade.THIRD;break;
            case 3: this.grade=Grade.SECOND;break;
            case 4: this.grade=Grade.FIRST;break;
        }
    }

    // 멀티 스레드를 위한 예시용 메서드
    public int addCash(int cash){
        this.cash += cash;
        return this.cash;
    }

    // permission lock 을 하기 위해서 @Version 어노테이션을 Entity인 Member 객체에 넣는다.
    // @Version 은 Entity 계층에 넣어야 한다는 점
    @Version
    private Integer version;

}
