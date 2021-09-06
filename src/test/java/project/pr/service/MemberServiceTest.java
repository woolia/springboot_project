package project.pr.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import project.pr.domain.Address;
import project.pr.domain.status.Grade;
import project.pr.domain.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Rollback(value = false)
class MemberServiceTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberService memberService;

    @Test
    void 회원가입(){
        Member member = new Member("userA", "A","1234" , new Address("street", "daejeon"), "010-4132-4235", Grade.FIRST, "zecrar@naver.com");
        memberService.join(member);

        Member findMember = memberService.findOne(member.getId());
        assertThat(findMember).isEqualTo(member);
    }


    @Test
    public void 중복회원체크() throws Exception{

        assertThrows(IllegalStateException.class , () ->{
            Member member1 = new Member("userA","A","1234" ,new Address("street1", "daejeon"), "010-4132-4235", Grade.FIRST, "zecrar@naver.com");
            Member member2 = new Member("userB","B","1234" ,new Address("street2", "daejeon"), "010-3245-1235", Grade.SECOND, "zecrar@naver.com");
            Member member3 = new Member("userC","C","1234", new Address("street3", "daejeon"), "010-8532-4215", Grade.THIRD, "zecrar@naver.com");

            memberService.join(member1);
            memberService.join(member2);
            memberService.join(member3);

            em.flush();
            em.clear();

            Member member4 = new Member("userD", "D","1234" ,new Address("street4", "daejeon"), "010-7845-1952", Grade.FOURTH, "zecrar@naver.com");
            memberService.join(member4); // 여기서 예외가 발생해야 함
        });
    }

    @Test
    public void 로그인(){

        Member member1 = new Member("Member","AA","1234" ,new Address("street1", "daejeon"), "010-4132-4235", Grade.FIRST, "zecrar@12.com");
        memberService.join(member1);

        Member loginMember = memberService.findLoginMember(member1.getLoginId(), member1.getPassword());

        System.out.println("loginMember.getLoginId() = " + loginMember.getLoginId());
        System.out.println("loginMember.getPassword() = " + loginMember.getPassword());

        Assertions.assertThat(loginMember).isEqualTo(member1);
        Assertions.assertThat(loginMember.getId()).isEqualTo(member1.getId());
    }


    @Test
    public void 로그인1(){

        Member member1 = new Member("Member","AA","1234" ,new Address("street1", "daejeon"), "010-4132-4235", Grade.FIRST, "zecrar@12.com");
        memberService.join(member1);

        Member loginMember = memberService.findLoginId(member1.getLoginId());

        Assertions.assertThat(loginMember).isEqualTo(member1);
        Assertions.assertThat(loginMember.getId()).isEqualTo(member1.getId());
        Assertions.assertThat(loginMember.getLoginId()).isEqualTo(member1.getLoginId());
    }


    @Test
    public void 중복아이디_로그인_처리(){

        Member member1 = new Member("유선범","A","tjsqja12" ,new Address("거리", "daejeon"), "010-4132-5555", Grade.FIRST, "!!!!!!!!.com");

        assertThrows(IllegalStateException.class , () -> {
            memberService.joinValidation(member1);
        });


//        assertThrows(IllegalStateException.class ,() ->{
//            if(memberService.findLoginId(member1.getLoginId()).equals(member1.getLoginId())){
//                memberService.join(member1);
//            }else {
//                System.out.println("=============================");
//                throw new IllegalStateException("이미 아이디가 존재합니다!!!.");
//            }
//        });
    }


}