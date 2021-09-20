package project.pr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.pr.api.dto.MemberDto;
import project.pr.auth.dto.SessionUser;
import project.pr.domain.Member;
import project.pr.repository.db.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    @Autowired
    private final MemberRepository memberRepository;
    @PersistenceContext
    private EntityManager em;

    // 회원가입
    @Transactional
    public Long join(Member member){
        memberRepository.save(member);
        return member.getId();
    }

    public Member findLoginId(String loginId){
        Member member = memberRepository.findByLoginId(loginId).orElse(null);
        return member;
    }

    @Transactional
    public void joinValidation(Member member) {
        Member findMember = memberRepository.findByLoginId(member.getLoginId()).orElse(null);

        if(findMember != null && findMember.getLoginId().equals(member.getLoginId())){
            log.info("이미 등록된 회원입니다.");
            throw new IllegalStateException("이미 등록된 회원입니다.");
        }else {
            join(member);
        }

    }

    // 전체 회원 조회
    public List<Member> findMembers(){
        return  memberRepository.findAll();
    }

    // 회원 한명 조회
    public Member findOne(Long id){
        return memberRepository.findById(id).orElse(null);
    }

    public Member findLoginMember(String loginId , String password) {
        Member member = memberRepository.findByLoginMember(loginId, password).orElse(null);
        return member;
    }


    // 회원 삭제
    @Transactional
    public void deleteMember(Long id){
        memberRepository.deleteById(id);
    }

    // 전체 회원 삭제
    @Transactional
    public void deleteAllMember(){
        memberRepository.deleteAll();
    }

    @Transactional
    public MemberDto updateMember(Long id , MemberDto memberDto) {
        Member member = memberRepository.findById(id).orElse(null);
        Member changeMember = member.changeinfo(memberDto.getId(), memberDto.getName(), memberDto.getEmail(), memberDto.getOrderCount());

        return new MemberDto(changeMember.getId(), changeMember.getName(), changeMember.getEmail(), changeMember.getOrderCount(), changeMember.getGrade());
    }

    // 멀티 스레드를 위한 예시용 메서드
    @Transactional
    public void findMemberaddCash(Long memberId , int cash) {
        Member member = memberRepository.findMemberById(memberId).orElseThrow(() -> new IllegalStateException());
        // memberRepository의 findById() 메서드는  pessimistic lock 을 걸어놓은 repository method 이다.
        int currentCash = member.getCash();
        log.info("현재 스레드 : {} , 입금전잔액 : {}" , Thread.currentThread().getName() , currentCash);
        int result = member.addCash(cash);
        log.info("현재 스레드 : {} , 입금후잔액 : {}" , Thread.currentThread().getName() , result);
        memberRepository.save(member);
    }

    public Member findEmail(String email) {
        Member member = memberRepository.findLoginFormByEmail(email).orElse(null);
        return member;
    }
}
