package project.pr.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import project.pr.domain.Member;
import project.pr.repository.db.custom.MemberRepositoryCustom;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member , Long> , MemberRepositoryCustom {


    // Repository 계층에 @Lock 을 추가한다.
    // 현재 findById로 찾기 때문에 JpaRepository를 사용하면 구현할 필요가 없지만 @Lock 어노테이션을 넣을 수 없어서
    // 구현
    // @Lock 으로 인해 다른 동시성 요청이 들어온다면 무조건 차단될 것
    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    Optional<Member> findMemberById(Long id);

    Optional<Member> findByName(String name);
    // 이렇게 하면 자동으로 스프링 데이터 JPA가 해당 메서드를 만들어줌

    List<Member> findByEmail(String email);


    Optional<Member> findByLoginId(String LoginId);

}
