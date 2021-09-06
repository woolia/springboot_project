package project.pr.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import project.pr.service.discountpolicy.DiscountPolicy;
import project.pr.service.discountpolicy.FixDiscountPolicy;
import project.pr.service.discountpolicy.RateDiscountPolicy;

@Configuration
public class OrderApp {

    @Bean
    public DiscountPolicy discountPolicy(){
        return new RateDiscountPolicy();
        //return new FixDiscountPolicy();
    }

}
