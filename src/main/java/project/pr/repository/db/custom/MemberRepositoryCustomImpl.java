package project.pr.repository.db.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import project.pr.domain.Member;
import project.pr.domain.QMember;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

import static project.pr.domain.QMember.*;


public class MemberRepositoryCustomImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @PersistenceContext
    EntityManager em;

    public MemberRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<Member> findByLoginMember(String loginId, String password) {

        Member member = queryFactory.select(QMember.member).from(QMember.member)
                .where(QMember.member.loginId.eq(loginId).
                        and(QMember.member.password.eq(password)))
                .fetchOne();

        return Optional.ofNullable(member);
    }
}
