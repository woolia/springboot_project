package project.pr.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.pr.domain.Address;
import project.pr.domain.status.Grade;
import project.pr.domain.Member;
import project.pr.repository.db.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @PersistenceContext EntityManager em;

    @BeforeEach
    void Member(){

        for (int i = 0; i < 4; i++) {
            if (i % 4 == 0) {
                Member member = new Member("member" + i,"A","1234", new Address("street" + i, "city" + i), "010-1234-000" + i, Grade.FIRST , "member"+i+"@naver.com");
                memberRepository.save(member);
            }
            if (i % 4 == 1) {
                Member member = new Member("member" + i,"A","1234", new Address("street" + i, "city" + i), "010-1234-000" + i, Grade.SECOND,"member"+i+"@naver.com");
                memberRepository.save(member);
            }
            if (i % 4 == 2) {
                Member member = new Member("member" + i,"A","1234", new Address("street" + i, "city" + i), "010-1234-000" + i, Grade.THIRD,"member"+i+"@naver.com");
                memberRepository.save(member);
            }
            if (i % 4 == 3) {
                Member member = new Member("member" + i,"A","1234", new Address("street" + i, "city" + i), "010-1234-000" + i, Grade.FOURTH,"member"+i+"@naver.com");
                memberRepository.save(member);
            }
        }

    }

    //  멤버를 저장하는 테스트
    @Test
    void saveMember(){

        Member member = new Member("userA","A","1234", new Address("kk", "Daejeon"), "010-1234-4343", Grade.FIRST,"member1@naver.com");
        memberRepository.save(member);

        Member findMember = memberRepository.findById(member.getId()).orElse(null);


        assertThat(findMember).isEqualTo(member);
    }

    // 멤버를 찾는 테스트
    @Test
    void findMembers(){

        List<Member> members = memberRepository.findAll();

        assertThat(members.size()).isEqualTo(4);

        Member findMember = members.stream().filter(m -> m.getGrade() == Grade.FIRST).findAny().orElse(null);
        // members 를 stream 으로 바꾸고 fiter로 member의 Grade가 FIRST 인 맴버를 찾고 없으면  null 로 반환
        // 위에 @BeforeEach로 맴버를 만들어서 넣어서 Grade.FIRST 인 맴버는 다음과 같을 것이다.
        // Member member = new Member("member" + i, new Address("street" + i, "city" + i), "010-1234-000" + i, Grade.FISRT)

        assertThat(findMember.getGrade()).isEqualTo(Grade.FIRST);

        // 다른 정보로 Grade.FIRST 인 맴버를 찾아왔는지 확인
        assertThat(findMember.getName()).isEqualTo("member0");
        assertThat(findMember.getAddress().getCity()).isEqualTo("city0");
        assertThat(findMember.getAddress().getStreet()).isEqualTo("street0");
        assertThat(findMember.getPhoneNumber()).isEqualTo("010-1234-0000");
    }

    // 멤버를 삭제하는 테스트
    @Test
    void delete(){
        List<Member> members1 = memberRepository.findAll();
        Member findMember1 = memberRepository.findById(1L).orElse(null);
        System.out.println("findMember1 = " + findMember1);

        assertThat(members1.size()).isEqualTo(4);
        // 삭제하기 전에 테스트

        memberRepository.delete(findMember1);
        // 삭제를 했지만 현재 1차 캐시에는 기존 4개의 데이터가 남아있기 때문에 DB에서 다시 불러와야 한다.

        List<Member> members2 = memberRepository.findAll();
        assertThat(members2.size()).isEqualTo(3);
    }


    // 멤버 이름으로 찾는 테스트
    @Test
    void findMemberByName(){
        Member userA = new Member("userA", "A","1234",null, null, Grade.FOURTH,"member1@naver.com");
        memberRepository.save(userA);

        Member findMember = memberRepository.findByName("userA").orElse(null);
        assertThat(findMember.getName()).isEqualTo("userA");
        assertThat(findMember.getGrade()).isEqualTo(Grade.FOURTH);
    }

    // 멤버 수정하는 테스트(update)
    // 스프링 부트는 변경감지라는 기능을 사용한다.
    @Test
    void Update(){


        Member findMember1 = memberRepository.findByName("member1").orElse(null);
        System.out.println("findMember1 = " + findMember1.getPhoneNumber());
        System.out.println("findMember1 = " + findMember1.getGrade());

        findMember1.change(findMember1.getName(), findMember1.getAddress(),
                            "010-3453-5454", Grade.FIRST , findMember1.getEmail());

        // 여기서 update 할때는 반드시 flush() 가 되어야 한다.
        // 현재는 persist로 영속성 컨텍스트에서만 변경된 상태인데 이제 DB로 바꾸기 위해서는 @Transactional 을 사용하거나 em.flush() 로 커밋하는데
        // 지금은 테스트 이기 때문에 em.flush() 를 사용

        // 그리고 실무 강사님이 말하길 setter 는 막 열어두면 다른 사람들이 할때 막 사용해도 되는 줄 알고 사용해서
        // 종종 에러가 발생한다고 한다. 그래서 setter는 닫아두고 다른 메서드를 사용해서 변경감지를 사용해야 한다고 한다
        // 나는 change라는 메서드를 사용해서 변경감지를 사용했다.

        em.flush();
        em.clear();
        // clear를 통해 영속성 컨텍스트를 다 지우고 다시 DB로 부터 불러옴

        Member findMember2 = memberRepository.findByName("member1").orElse(null);
        // 같은 이름의 멤버를 다시 찾음

        System.out.println("findMember2 = " + findMember2.getPhoneNumber());
        System.out.println("findMember2 = " + findMember2.getGrade());

        /* 콘솔을 확인해보면 바뀌었다 하지만 같은 객체일까??
        findMember1 = 010-1234-0001
        findMember1 = SECOND

        findMember2 = 010-3453-5454
        findMember2 = FISRT

        id 값은 우리가 지정하는게 아니라 DB에서 자동으로 생성되는 것이기 때문에 변하지 않는다
        따라서 같은 id 인지 확인하면 된다.
        */

        assertThat(findMember2.getId()).isEqualTo(findMember1.getId());
    }


    // 이메일로 찾기
    @Test
    void findByEmail(){

        Member newMember = new Member("유선범", "A","1234",null, "010-1234-1234", Grade.SECOND, "zecrar@naver.com");
        memberRepository.save(newMember);

        List<Member> findMembers = memberRepository.findByEmail(newMember.getEmail());

        Member findMember = findMembers.stream().findAny().orElse(null);

        assertThat(findMember).isEqualTo(newMember);

        assertThat(findMember.getEmail()).isEqualTo(newMember.getEmail());
        assertThat(findMember.getGrade()).isEqualTo(Grade.SECOND);
        assertThat(findMember.getName()).isEqualTo("유선범");
    }

}