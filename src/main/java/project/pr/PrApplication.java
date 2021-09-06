package project.pr;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import project.pr.domain.Address;
import project.pr.domain.Item;
import project.pr.domain.Member;
import project.pr.domain.status.Grade;
import project.pr.service.ItemService;
import project.pr.service.MemberService;
import project.pr.service.OrderService;

import javax.annotation.PostConstruct;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class PrApplication {
	public static void main(String[] args) {
		SpringApplication.run(PrApplication.class, args);
	}
}


