package project.pr.service.discountpolicy;


import lombok.extern.slf4j.Slf4j;
import project.pr.domain.status.Grade;
import project.pr.domain.Member;

@Slf4j
public class RateDiscountPolicy implements DiscountPolicy{

    int finalDiscount;

    @Override
    public int discount(Member member, int price) {

        int basicDiscountPercent = 5;
        Grade grade = member.getGrade();

        log.info("price = {}" , price);

        if(member.getGrade() == Grade.NORMAL){
            return price;
        }

        switch (grade){
            case FIRST: finalDiscount = basicDiscountPercent * 4; break;
            case SECOND: finalDiscount = basicDiscountPercent * 3; break;
            case THIRD: finalDiscount = basicDiscountPercent * 2; break;
            case FOURTH: finalDiscount = basicDiscountPercent * 1; break;
        }

        log.info("최종 할인 = {}%" , finalDiscount);
        log.info("할인 가격 = {}" , (price * finalDiscount / 100));

        if (price - finalDiscount <= 5000) {
            throw new IllegalStateException("가격 할인이 될 수 없습니다.");
        }

        int discountedPrice = price - (price * finalDiscount / 100);
        log.info("최종 가격 = {}" , discountedPrice);
        return discountedPrice;
    }
}
