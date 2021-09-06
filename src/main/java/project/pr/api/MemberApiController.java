package project.pr.api;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import project.pr.api.apiexception.ErrorResult;
import project.pr.api.apiexception.MemberResult;
import project.pr.api.dto.MemberDto;
import project.pr.domain.Member;
import project.pr.service.MemberService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/members")
    public MemberResult<MemberDto> MembersApi(){

        List<Member> members = memberService.findMembers();

        List<MemberDto> memberDtos = members.stream().map(m -> new MemberDto(m.getId(), m.getName(), m.getEmail(), m.getOrderCount(), m.getGrade()))
                .collect(Collectors.toList());

        return new MemberResult(memberDtos);
    }

    @PostMapping("/{id}")
    public MemberDto findMemberByJson(@PathVariable Long id){

        Member findMember = memberService.findOne(id);
        log.info("findMember = {}",findMember);

        if(findMember == null){
            throw new NullPointerException("회원이 존재하지 않습니다.");
        }

        return new MemberDto().addMember(findMember);
    }
    // 리스트의 경우에는 어떤 임의의 클래스로 감싸서 json 방식으로 반환해줘야 한다.


    // DeleteMapping 으로 데이터를 삭제하기

    @DeleteMapping("/{id}")
    public void deleteApiMember(@PathVariable Long id){
        log.info("member = {}" , memberService.findOne(id));
        memberService.deleteMember(id);
        log.info("members = {}" , memberService.findMembers());
    }

    @DeleteMapping("/delete")
    public void AlldeleteApiMember(){
        memberService.deleteAllMember();
    }


    // PutMapping 으로 회원 데이터 수정하기
    @PutMapping("/{id}")
    public MemberDto updateMember(@RequestBody MemberDto memberDto , @PathVariable Long id){
        MemberDto Member = memberService.updateMember(id, memberDto);
        return Member;
    }



}
