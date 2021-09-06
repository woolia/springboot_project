package project.pr.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@Slf4j
@RequestMapping("/error")
public class ErrorController {


    @GetMapping("/404")
    public void error404(HttpServletResponse response) throws IOException {
        log.info("404 에러 발생");
        response.sendError(404 , "404 에러");
    }

    @GetMapping("/400")
    public void error4xx(HttpServletResponse response) throws IOException {
        log.info("4xx 에러 발생");
        response.sendError(400 , "4xx 에러");
    }

    @GetMapping("/500")
    public void error5xx(HttpServletResponse response) throws IOException {
        log.info("5xx 에러 발생");
        response.sendError(500 , "500에러");
    }

}
