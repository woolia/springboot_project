package project.pr.api.excontrolleradvice;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import project.pr.api.apiexception.ErrorResult;

@Slf4j
@RestControllerAdvice
public class ExControllerAdvice {


    @ExceptionHandler(NullPointerException.class)
    public ErrorResult NullPointerExceptionHandler(NullPointerException e){
        log.error("NullPointerExceptionHandler ex ",e);
        return new ErrorResult("에러" , e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResult exHandler(Exception e){
        log.error("exceptionHandler ex ",e);
        return new ErrorResult("EX", "내부 오류");
    }

}
