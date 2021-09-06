package project.pr.service.discountpolicy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import project.pr.domain.status.Grade;
import project.pr.domain.Member;

@Component
@Slf4j
public class FixDiscountPolicy implements DiscountPolicy{

    int finalDiscount;

    @Override
    public int discount(Member member, int price) {
        log.info("discount.price = {}" , price);

        int basicDiscount = 1000;
        Grade grade = member.getGrade();
        log.info("grade = {}" , grade);

        if (member.getGrade() == Grade.NORMAL){
            return price;
        }
        // 일반 등급 회원일때 할인 없음

        switch (grade){
            case FIRST: finalDiscount = basicDiscount * 4 ; break;
            case SECOND: finalDiscount = basicDiscount * 3; break;
            case THIRD: finalDiscount = basicDiscount * 2; break;
            case FOURTH: finalDiscount = basicDiscount * 1; break;
        }

        log.info("finalDiscount = {}" , finalDiscount);

        if (price - finalDiscount <= 5000) {
            throw new IllegalStateException("가격 할인이 될 수 없습니다.");
        }

        int discountedPrice = price - finalDiscount;

        log.info("discountedPrice = {}" , discountedPrice);
        return discountedPrice;
    }
}
