package project.pr.repository.db.custom;

import org.springframework.data.jpa.repository.Lock;
import project.pr.domain.Member;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface MemberRepositoryCustom {

    Optional<Member> findByLoginMember(String loginId , String password);

}
