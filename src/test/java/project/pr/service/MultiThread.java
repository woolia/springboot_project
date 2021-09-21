package project.pr.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import project.pr.domain.Address;
import project.pr.domain.Member;
import project.pr.domain.status.Grade;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MultiThread {

    @Autowired
    MemberService memberService;
    @Autowired
    ItemService itemService;

    private static final ExecutorService service = Executors.newFixedThreadPool(30);
    // 스레드 30개 생성
    
    Long memberId;

    @BeforeEach
    void init(){
        Long memberid = memberService.join(new Member("유선범", "AAA", "1234", new Address("street", "city"), "010-4652-6327",
                Grade.NORMAL, "zecrar@naver.com", 0));
        memberId = memberid;
    }
    // 멀티스레드의 예를 들어서 member에 잔액을 추가해서 0 원을 넣는다


    @Test
    void test() throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(30);
        /*
        CountDownLatch는 언제 쓸까?
        쓰레드를 N개 실행했을 때, 일정 개수의 쓰레드가 모두 끝날 때 까지 기다려야지만
        다음으로 진행할 수 있거나 다른 쓰레드를 실행시킬 수 있는 경우 사용한다.
         */

        /*
        쓰레드는 마지막에서 countDown() 메서드를 불러준다.
        그러면 초기화 때 넣어준 정수값이 하나 내려간다.
        즉 각 쓰레드는 마지막에서 자신이 실행완료했음을 countDown 메서드로 알려준다.
        이 쓰레드들이 끝나기를 기다리는 쪽 입장에서는 await()메서드를 불러준다.
        그러면 현재 메서드가 실행중인 메인쓰레드는 더이상 진행하지않고 CountDownLatch의 count가 0이 될 때까지 기다린다.
         */

        for (int i = 0; i < countDownLatch.getCount(); i++) {
            // 위에 만들어놓은 예제의 아이디에 여러곳에서 돈을 1000원씩 넣는다고 예를 들어보자(그냥 멀티 스레드를 위한 예시)
            service.execute(() ->{
                memberService.findMemberaddCash(memberId , 1000);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();


        // 동시성을 해결하지 않으면 예상되는 값은 당연히 1000원이 30 번 들어가서 30000원 이 되어야 하는데 실제 값은 2000,3000,4000 원 이 나온다
        // 그렇기 때문에 동시성을 해결해야 한다.
        /*
        이를 해결하기 위한 lock은 jpa에 2가지가 있다고 한다.
        1. Optimistic Locking  , 2. Pessimistic Locking
         */

        //service.awaitTermination(2, TimeUnit.SECONDS);
        System.out.println("끝");

        /*
        중요한것은 아닌것 같지만 test에서 awaitTermination 을 사용해서
        지정된 시간 동안대기 하며 모든 작업이 모든 중지되었는지 체크 한다.

        현재 테스트에서 위에 스레드가 실행하는 도중에
        Member findMember = memberService.findOne(memberId); 가 실행되어서
        findMember를 가져온다.

        만약에 돈이 25000원일때의 데이터를 가져오면 DB상의 돈은 30000 이지만 현재 데이터를 가져온 findMember의 돈은 25000이 되어서
        테스트가 실패한다.

        이것때문에 계속 테스트가 실패하였는데 awaitTermination()를 사용해서 내가 원하는 숫자만큼의 초만큼 대기해서
        모든 스레드가 진행하였는지 체크하였다. 다른방식으로 sleep() 등을 사용할 수 있을 것 같다.


        굳이 테스트가 아니더라도 실제 웹에서 돈이나 다른것들의 동시성 문제가 발생할때 사용할수도 있을것같다.
        하지만 내가 사용한 방법은 요청이 크게 많지 않을때 같아서
        요청이 무수히 많아지면 위의 스레드가 모두 실행되고나서 메인스레드가 실행되는 방식을 사용해야 할 것 같다.
        이 방법은 공부해 보자.


        알고보니까 위에서
        new CountDownLatch(30); 을
        new CountDownLatch(20);
        로 지정한것이 문제였다.
        위에 설명을 보면

        /
        CountDownLatch는 언제 쓸까?
        쓰레드를 N개 실행했을 때, 일정 개수의 쓰레드가 모두 끝날 때 까지 기다려야지만
        다음으로 진행할 수 있거나 다른 쓰레드를 실행시킬 수 있는 경우 사용한다.
        /
        인데 그러면 new CountDownLatch(20); 일때는 쓰레드를 20개까지만 끝날때까지 기다려야 다음 쓰레드를 실행한다는 것인데
        현재 예시에서 쓰레드는 30으로 설정하였기 때문에 당연히 20개밖에 카운트 되지 않아서 20의 쓰레드만 끝날때까지 기다리는것

        근데 이상한것이 메인 쓰레드의 findMember의 값을 25000 일때 찾는다.
        내 예상은 cash값이 20000일때 찾을것으로 예상하였는데

        아무튼 안의 값을 30으로 바꾸니 위의 awaitTermination() 메서드를 주석처리 하더라도 30개의 쓰레드가 진행될때까지 기다리므로
        테스트가 성공한다.


        또한 오류예방으로 for문의 범위값을 30으로 임의로 설정하는것이 아니라 countDownLatch.getCount()로 설정하면
        어떤 쓰레드값이 들어오더라도 동적으로 countDownLatch가 실행될 것 이다.
         */

        Member findMember = memberService.findOne(memberId);
        System.out.println("findMember.getCash() = " + findMember.getCash());
        assertThat(findMember.getCash()).isEqualTo(1000 * 30);
    }


    /*
    멀티 스레드 동시성 요청이 들어올때 JPA 데이터베이스에서의 해결방법
    1. Entity

    @Version
    private Integer version;

    @Version 어노테이션을 넣는다. 나의 예제에서는 Member 엔티티에 삽입

    2. Repository

    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    Optional<Member> findMemberById(Long id);

    @Lock 을 넣어준다. 현재 예제에서는 Perssimistic lock 을 사용하였고 해당 lock은 동시성 요청이 들어오면 무조건 차단하는 원칙을 가진다.

    또한 현재 Jpa데이터의 JpaRepository<Member,Long> 인터페이스를 상속받고 있기 때문에 기존의 findById(Long id)를 사용하지 못해서
    대책으로 findMemberById(Long id)를 사용했다.
    Jpa 원칙상 find_By 사이에는 어떠한 글자도 들어올 수 있으면 해당 원칙을 지키고 마지막으로 Id만 넣으면 findById == find~~ById 가 된다.

    3. Service
    Service 계층에서는 크게 할것이 없고 단지 위의 Repository에서 @Lock 을 걸어놓은 메서드만 실행해주면 된다. 만약에 다른 메서드를 사용하게 되면
    에러로 여러개의 스레드 요청이 발생한다는 에러가 발생한다.


     */


}
