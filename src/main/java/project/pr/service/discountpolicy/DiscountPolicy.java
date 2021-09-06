package project.pr.service.discountpolicy;

import project.pr.domain.Member;

public interface DiscountPolicy {

    int discount(Member member , int price);

}
